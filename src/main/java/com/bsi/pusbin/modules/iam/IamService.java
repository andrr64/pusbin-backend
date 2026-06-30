package com.bsi.pusbin.modules.iam;

import com.bsi.pusbin.modules.iam.schema.LoginRequest;
import com.bsi.pusbin.modules.iam.schema.RegisterRequest;
import com.bsi.pusbin.shared.exception.db.DuplicateResourceException;
import com.bsi.pusbin.shared.exception.service.UnauthorizedException;
import com.bsi.pusbin.shared.security.JwtProperties;
import com.bsi.pusbin.shared.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Service untuk modul IAM (Identity and Access Management).
 * Di sinilah seluruh alur otentikasi, enkripsi password, manajemen JWT, 
 * rotasi refresh token, dan pembatasan laju IP (Rate Limiter) diatur.
 * 
 * ALUR PENTING UNTUK MAHASISWA:
 * 1. PENGAMANAN PASSWORD: Kita menggunakan Argon2PasswordEncoder (pemenang Password Hashing Competition)
 *    yang memiliki pertahanan sangat kuat terhadap serangan Brute Force berbasis GPU/ASIC.
 * 2. MANAJEMEN TOKEN (JWT):
 *    - Access Token: Token berumur pendek (e.g. 15 menit) untuk otentikasi request API.
 *    - Refresh Token: Token berumur panjang (e.g. 3 hari) disimpan di cookie untuk memperbarui Access Token.
 *    - Refresh Token Rotation: Setiap kali refresh token digunakan, kita menghapus token lama dari DB
 *      dan menggantinya dengan yang baru. Ini mencegah pencurian / replikasi token (Replay Attack).
 * 3. HASHING REFRESH TOKEN DI DATABASE: Kita TIDAK boleh menyimpan refresh token dalam bentuk teks polos (plain text) 
 *    di database. Jika database diretas, peretas bisa menggunakan token tersebut langsung. Oleh sebab itu, 
 *    kita hanya menyimpan hash satu-arah dari refresh token tersebut.
 */
@Service
@RequiredArgsConstructor
public class IamService {

    private final IamRepository iamRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final Argon2PasswordEncoder passwordEncoder;
    private final RateLimiter rateLimiter;

    /**
     * Mendaftarkan pengguna (admin) baru ke sistem.
     */
    public void register(RegisterRequest req, String ip) {
        // 1. Periksa laju request (Rate Limit) IP Address pengirim
        rateLimiter.check("register", ip);
        
        if (req.nip() == null || !req.nip().trim().matches("^\\d{18}$")) {
            throw new IllegalArgumentException("NIP harus terdiri dari 18 digit angka");
        }
        
        // 2. Validasi apakah NIP sudah pernah terdaftar di database
        if (iamRepository.existsByNip(req.nip())) {
            throw new DuplicateResourceException("NIP sudah terdaftar");
        }
        
        // 3. Enkripsi password menggunakan Argon2, lalu simpan ke database
        iamRepository.saveAdmin(req.nip(), passwordEncoder.encode(req.password()));
    }

    /**
     * Memproses login pengguna.
     */
    public void login(LoginRequest req, String ip, HttpServletResponse res) {
        // 1. Periksa laju request IP pengirim
        rateLimiter.check("login", ip);

        // 2. Periksa apakah akun ini sedang dikunci akibat terlalu banyak login gagal.
        //    Pertahanan ini tetap berlaku walau penyerang berganti-ganti alamat IP.
        rateLimiter.checkAccountLockout(req.nip());

        // 3. Cari data admin berdasarkan NIP
        IamRepository.AdminRecord admin = iamRepository.findByNip(req.nip())
                .orElseThrow(() -> {
                    rateLimiter.recordFailedLogin(req.nip());
                    return new UnauthorizedException("NIP atau password salah");
                });

        // 4. Verifikasi apakah password cocok dengan hash yang tersimpan di DB
        if (!passwordEncoder.matches(req.password(), admin.passwordHash())) {
            rateLimiter.recordFailedLogin(req.nip());
            throw new UnauthorizedException("NIP atau password salah");
        }

        // 5. Login berhasil, hapus riwayat kegagalan login akun ini
        rateLimiter.resetFailedLogin(req.nip());

        // 6. Generate token JWT baru jika otentikasi sukses
        String accessToken  = jwtProvider.generateAccessToken(admin.nip());
        String refreshToken = jwtProvider.generateRefreshToken(); // UUID acak
        String tokenHash    = jwtProvider.hashRefreshToken(refreshToken); // Hash satu-arah untuk disimpan di DB
        Timestamp expiresAt = Timestamp.from(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiryMs()));

        // 7. Simpan hash refresh token ke database
        iamRepository.saveRefreshToken(admin.id(), tokenHash, expiresAt);

        // 8. Set access_token dan refresh_token sebagai HttpOnly Cookies di response
        setTokenCookies(res, accessToken, refreshToken);
    }

    /**
     * Memperbarui Access Token yang kadaluarsa (Token Rotation).
     */
    public void refresh(String refreshToken, HttpServletResponse res) {
        // 1. Validasi keberadaan refresh token di cookie
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token tidak ditemukan");
        }
        
        // 2. Hash refresh token dan cari di database untuk verifikasi kepemilikan
        String hash = jwtProvider.hashRefreshToken(refreshToken);
        String nip  = iamRepository.findNipByRefreshToken(hash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token tidak valid atau sudah kadaluarsa"));

        // 3. Generate pasangan Access Token dan Refresh Token yang baru
        String newAccessToken  = jwtProvider.generateAccessToken(nip);
        String newRefreshToken = jwtProvider.generateRefreshToken();
        String newHash         = jwtProvider.hashRefreshToken(newRefreshToken);
        Timestamp expiresAt    = Timestamp.from(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiryMs()));

        // 4. ROTASI TOKEN: Hapus refresh token lama dari database (Single Use Only)
        iamRepository.deleteRefreshToken(hash);
        
        // 5. Simpan refresh token yang baru ke database
        IamRepository.AdminRecord admin = iamRepository.findByNip(nip)
                .orElseThrow(() -> new UnauthorizedException("Admin tidak ditemukan"));
        iamRepository.saveRefreshToken(admin.id(), newHash, expiresAt);

        // 6. Tulis cookie baru ke response
        setTokenCookies(res, newAccessToken, newRefreshToken);
    }

    /**
     * Memproses keluar sistem (logout).
     */
    public void logout(HttpServletResponse res) {
        // Hapus cookie otentikasi dari browser (dengan menyetel MaxAge = 0)
        clearTokenCookies(res);
    }

    /**
     * Menulis token otentikasi ke dalam HTTP Cookies.
     */
    private void setTokenCookies(HttpServletResponse res, String accessToken, String refreshToken) {
        res.addHeader(HttpHeaders.SET_COOKIE, buildCookie("access_token",  accessToken,  (int)(jwtProperties.getAccessTokenExpiryMs()  / 1000)));
        res.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refresh_token", refreshToken, (int)(jwtProperties.getRefreshTokenExpiryMs() / 1000)));
    }

    /**
     * Menghapus token otentikasi dengan menyetel isi cookie kosong dan masa aktif 0 detik.
     */
    private void clearTokenCookies(HttpServletResponse res) {
        res.addHeader(HttpHeaders.SET_COOKIE, buildCookie("access_token",  "", 0));
        res.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refresh_token", "", 0));
    }

    /**
     * Utility untuk membangun header Set-Cookie dengan bendera pengamanan ketat.
     *
     * - SameSite=Strict: Cookie tidak akan dikirim pada request lintas situs (cross-site),
     *   sehingga skrip otomatis/agen AI yang berjalan dari domain lain tidak bisa memicu
     *   request bercookie ke endpoint ini (mitigasi CSRF).
     */
    private String buildCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)   // Mencegah akses via JavaScript (Anti XSS)
                .secure(true)     // Hanya dikirimkan melalui jalur HTTPS yang terenkripsi (Anti MitM/Sniffing)
                .path("/")        // Cookie berlaku untuk seluruh path URL di server kita
                .maxAge(maxAge)   // Mengatur masa aktif cookie (dalam detik)
                .sameSite("Strict")
                .build()
                .toString();
    }
}


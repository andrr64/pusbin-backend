package com.bsi.pusbin.modules.iam;

import com.bsi.pusbin.modules.iam.schema.LoginRequest;
import com.bsi.pusbin.modules.iam.schema.RegisterRequest;
import com.bsi.pusbin.shared.response.APIResponse;
import com.bsi.pusbin.shared.security.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller untuk modul IAM (Identity and Access Management / Keamanan & Otentikasi).
 * Controller ini menangani proses pendaftaran akun (register), masuk (login), pembaruan token (refresh), 
 * dan keluar (logout) menggunakan pengamanan berbasis HTTP Cookies (HttpOnly & Secure).
 * 
 * PENJELASAN UNTUK MAHASISWA:
 * - Mengapa menggunakan Cookie HttpOnly + Secure?: Menyimpan Access Token di Cookie jauh lebih aman dibanding 
 *   di LocalStorage/SessionStorage karena cookie HttpOnly TIDAK BISA diakses oleh JavaScript client. 
 *   Hal ini melindung token dari serangan pencurian token lewat XSS (Cross-Site Scripting).
 * - IP Address Tracking: Kita mengambil alamat IP user lewat `httpReq.getRemoteAddr()` untuk kebutuhan
 *   Rate Limiting demi mencegah serangan Brute Force.
 */
@RestController
@RequestMapping("/api/v1/iam")
@RequiredArgsConstructor
public class IamController {

    private final IamService iamService;

    /**
     * Endpoint untuk pendaftaran user baru.
     * HTTP Method: POST
     */
    @PostMapping("/register")
    public ResponseEntity<APIResponse<Void>> register(
            @RequestBody @Valid RegisterRequest req,
            HttpServletRequest httpReq) {
        // Meneruskan request registrasi beserta IP Address pengirim ke service.
        iamService.register(req, httpReq.getRemoteAddr());
        return ResponseEntity.ok(APIResponse.ok(null, "Akun berhasil dibuat"));
    }

    /**
     * Endpoint untuk login user.
     * HTTP Method: POST
     * 
     * - HttpServletResponse httpRes: Kita butuh object response servlet asli untuk meletakkan cookie
     *   otentikasi langsung ke browser client.
     */
    @PostMapping("/login")
    public ResponseEntity<APIResponse<Void>> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest httpReq,
            HttpServletResponse httpRes) {
        // Melakukan proses otentikasi login. Cookie akan diset langsung ke dalam response servlet.
        iamService.login(req, httpReq.getRemoteAddr(), httpRes);
        return ResponseEntity.ok(APIResponse.ok(null, "Login berhasil"));
    }

    /**
     * Endpoint untuk memperbarui Access Token yang kadaluarsa menggunakan Refresh Token.
     * HTTP Method: POST
     * 
     * - @CookieValue(value = "refresh_token"): Membaca secara otomatis isi Cookie bernama "refresh_token" 
     *   yang dikirimkan browser.
     */
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<Void>> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse httpRes) {
        // Memproses rotasi/refresh token. Cookie baru akan ditulis kembali ke response servlet.
        iamService.refresh(refreshToken, httpRes);
        return ResponseEntity.ok(APIResponse.ok(null, "Token diperbarui"));
    }

    /**
     * Endpoint untuk logout (keluar sistem).
     * HTTP Method: POST
     * 
     * - @Auth: Memastikan endpoint logout hanya bisa diakses oleh user yang sudah otentik (login).
     */
    @PostMapping("/logout")
    @Auth
    public ResponseEntity<APIResponse<Void>> logout(HttpServletResponse httpRes) {
        // Memanggil service untuk menghapus (menghapus cookie) otentikasi client.
        iamService.logout(httpRes);
        return ResponseEntity.ok(APIResponse.ok(null, "Berhasil logout"));
    }
}


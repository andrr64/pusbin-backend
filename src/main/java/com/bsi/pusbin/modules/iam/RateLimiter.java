package com.bsi.pusbin.modules.iam;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Komponen pembatas laju request (Rate Limiter) dan penjaga lockout akun.
 * Komponen ini bertugas mencegah serangan Brute Force / Credential Stuffing - termasuk yang
 * dijalankan oleh agen otomatis berbasis AI yang mencoba ribuan kombinasi NIP/password secara
 * cepat dan/atau dari banyak alamat IP berbeda (rotating proxy).
 *
 * PENJELASAN UNTUK MAHASISWA:
 * - Thread-Safety: Karena server Spring Boot menangani banyak request secara asinkronus (multi-threaded),
 *   kita tidak boleh menggunakan HashMap biasa karena rentan rusak akibat rebutan resource (Race Condition).
 *   Oleh karena itu, kita wajib menggunakan `ConcurrentHashMap` yang aman digunakan oleh banyak thread sekaligus.
 * - @Value("${...}"): Mengambil konfigurasi dari file `application.properties` atau `application.yml`
 *   dengan nilai default jika konfigurasi tidak ditemukan (e.g. default 5 kali percobaan).
 * - in-memory Rate Limiting: Penyimpanan data limit di simpan dalam memori server (RAM) menggunakan ConcurrentHashMap.
 * - DUA LAPIS PERTAHANAN:
 *   1. Per-IP throttling (check): membatasi laju request dari satu alamat IP. Ini menahan
 *      serangan dari satu sumber, tapi tidak cukup melawan botnet/AI agent yang berganti-ganti IP.
 *   2. Per-akun lockout (checkAccountLockout/recordFailedLogin): mengunci SATU akun (NIP) sementara
 *      setelah beberapa kali gagal login berturut-turut, terlepas dari IP mana pun yang mencoba.
 * - PEMBERSIHAN OTOMATIS (cleanup): entri yang sudah kadaluarsa dibersihkan secara berkala lewat
 *   @Scheduled agar ConcurrentHashMap tidak bertumbuh tanpa batas (mencegah memory-exhaustion DoS).
 */
@Component
public class RateLimiter {

    // Pengaturan batas percobaan login (default: maksimal 5 kali dalam 60 detik)
    @Value("${iam.rate-limit.login.max-attempts:5}")
    private int loginMaxAttempts;

    @Value("${iam.rate-limit.login.window-seconds:60}")
    private int loginWindowSeconds;

    // Pengaturan batas percobaan register (default: maksimal 3 kali dalam 60 detik)
    @Value("${iam.rate-limit.register.max-attempts:3}")
    private int registerMaxAttempts;

    @Value("${iam.rate-limit.register.window-seconds:60}")
    private int registerWindowSeconds;

    // Pengaturan lockout akun (default: kunci akun selama 15 menit setelah 5 kali gagal dalam 5 menit)
    @Value("${iam.rate-limit.account-lockout.max-attempts:5}")
    private int accountLockoutMaxAttempts;

    @Value("${iam.rate-limit.account-lockout.window-seconds:300}")
    private int accountLockoutWindowSeconds;

    @Value("${iam.rate-limit.account-lockout.lockout-seconds:900}")
    private int accountLockoutSeconds;

    // Penyimpanan data sementara IP Address dan jumlah percobaannya (Store)
    private final ConcurrentHashMap<String, AttemptInfo> store = new ConcurrentHashMap<>();

    // Penyimpanan status lockout per akun (NIP)
    private final ConcurrentHashMap<String, AccountStatus> lockoutStore = new ConcurrentHashMap<>();

    /**
     * Memeriksa laju request berdasarkan aksi (action) dan alamat IP pengirim.
     * Jika melampaui batas, akan melempar exception HttpStatus.TOO_MANY_REQUESTS (HTTP 429).
     */
    public void check(String action, String ip) {
        int maxAttempts = action.equals("login") ? loginMaxAttempts : registerMaxAttempts;
        int windowSeconds = action.equals("login") ? loginWindowSeconds : registerWindowSeconds;

        // Membuat kunci unik gabungan aksi dan IP (e.g. "login:192.168.1.100")
        String key = action + ":" + ip;
        Instant now = Instant.now();

        // compute() adalah operasi thread-safe atomik untuk memperbarui nilai dalam ConcurrentHashMap
        AttemptInfo info = store.compute(key, (k, existing) -> {
            // Jika belum pernah mencoba, atau masa jendela blokir sebelumnya sudah terlewati (kadaluarsa):
            if (existing == null || now.isAfter(existing.windowEnd())) {
                // Inisialisasi percobaan ke-1 dengan waktu berakhir window yang baru
                return new AttemptInfo(1, now.plusSeconds(windowSeconds));
            }
            // Jika masih berada di dalam jendela waktu yang sama, tambahkan jumlah percobaan (+1)
            return new AttemptInfo(existing.count() + 1, existing.windowEnd());
        });

        // Jika jumlah percobaan melampaui batas maksimum yang ditentukan
        if (info.count() > maxAttempts) {
            throw new AppException("Terlalu banyak percobaan, coba lagi nanti", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    /**
     * Memeriksa apakah akun (NIP) sedang dalam status terkunci akibat terlalu banyak login gagal.
     * Dipanggil SEBELUM verifikasi password agar penyerang tidak bisa terus menebak password
     * sebuah akun meskipun mereka berganti-ganti alamat IP.
     */
    public void checkAccountLockout(String nip) {
        AccountStatus status = lockoutStore.get(nip);
        if (status != null && status.lockedUntil() != null && Instant.now().isBefore(status.lockedUntil())) {
            throw new AppException("Akun terkunci sementara karena terlalu banyak percobaan gagal, coba lagi nanti", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    /**
     * Mencatat satu kali percobaan login gagal untuk sebuah akun (NIP).
     * Jika jumlah kegagalan dalam satu jendela waktu melampaui batas, akun dikunci
     * selama durasi lockout yang ditentukan.
     */
    public void recordFailedLogin(String nip) {
        Instant now = Instant.now();
        lockoutStore.compute(nip, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowEnd())) {
                return new AccountStatus(1, now.plusSeconds(accountLockoutWindowSeconds), null);
            }
            int failureCount = existing.failureCount() + 1;
            Instant lockedUntil = failureCount >= accountLockoutMaxAttempts
                    ? now.plusSeconds(accountLockoutSeconds)
                    : existing.lockedUntil();
            return new AccountStatus(failureCount, existing.windowEnd(), lockedUntil);
        });
    }

    /**
     * Menghapus status kegagalan login sebuah akun (dipanggil setelah login berhasil).
     */
    public void resetFailedLogin(String nip) {
        lockoutStore.remove(nip);
    }

    /**
     * Membersihkan entri-entri yang sudah kadaluarsa secara berkala agar penyimpanan
     * di memori (ConcurrentHashMap) tidak bertumbuh tanpa batas.
     */
    @Scheduled(fixedRateString = "${iam.rate-limit.cleanup-interval-ms:300000}")
    public void cleanupExpiredEntries() {
        Instant now = Instant.now();
        store.entrySet().removeIf(e -> now.isAfter(e.getValue().windowEnd()));
        lockoutStore.entrySet().removeIf(e -> {
            AccountStatus s = e.getValue();
            Instant expiry = s.lockedUntil() != null ? s.lockedUntil() : s.windowEnd();
            return now.isAfter(expiry);
        });
    }

    /**
     * Java Record pembantu untuk membungkus data jumlah hit dan batas waktu (per-IP).
     */
    record AttemptInfo(int count, Instant windowEnd) {}

    /**
     * Java Record pembantu untuk membungkus status lockout per akun (NIP).
     */
    record AccountStatus(int failureCount, Instant windowEnd, Instant lockedUntil) {}
}

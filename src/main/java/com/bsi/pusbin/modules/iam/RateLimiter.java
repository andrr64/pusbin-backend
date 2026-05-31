package com.bsi.pusbin.modules.iam;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Komponen pembatas laju request (Rate Limiter).
 * Komponen ini bertugas mencegah serangan Brute Force (menebak password ribuan kali secara otomatis) 
 * dengan cara membatasi jumlah request yang masuk dari IP address yang sama dalam kurun waktu tertentu.
 * 
 * PENJELASAN UNTUK MAHASISWA:
 * - Thread-Safety: Karena server Spring Boot menangani banyak request secara asinkronus (multi-threaded),
 *   kita tidak boleh menggunakan HashMap biasa karena rentan rusak akibat rebutan resource (Race Condition).
 *   Oleh karena itu, kita wajib menggunakan `ConcurrentHashMap` yang aman digunakan oleh banyak thread sekaligus.
 * - @Value("${...}"): Mengambil konfigurasi dari file `application.properties` atau `application.yml` 
 *   dengan nilai default jika konfigurasi tidak ditemukan (e.g. default 5 kali percobaan).
 * - in-memory Rate Limiting: Penyimpanan data limit di simpan dalam memori server (RAM) menggunakan ConcurrentHashMap.
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

    // Penyimpanan data sementara IP Address dan jumlah percobaannya (Store)
    private final ConcurrentHashMap<String, AttemptInfo> store = new ConcurrentHashMap<>();

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
     * Java Record pembantu untuk membungkus data jumlah hit dan batas waktu.
     */
    record AttemptInfo(int count, Instant windowEnd) {}
}


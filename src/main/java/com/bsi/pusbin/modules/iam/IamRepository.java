package com.bsi.pusbin.modules.iam;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * Repository untuk modul IAM.
 * Bertanggung jawab mengelola tabel `users` (nip dan password hash) 
 * serta tabel `refresh_tokens` (penyimpanan hash dari refresh token yang aktif).
 */
@Repository
@RequiredArgsConstructor
public class IamRepository {

    private final JdbcTemplate jdbc;

    /**
     * Mencari data pengguna (UserRecord) berdasarkan NIP.
     */
    public Optional<UserRecord> findByNip(String nip) {
        try {
            UserRecord user = jdbc.queryForObject(
                    "SELECT id, nip, password_hash FROM users WHERE nip = ?",
                    (rs, i) -> new UserRecord(rs.getInt("id"), rs.getString("nip"), rs.getString("password_hash")),
                    nip);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Memeriksa apakah NIP sudah terdaftar di database.
     */
    public boolean existsByNip(String nip) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE nip = ?", Integer.class, nip);
        return count != null && count > 0;
    }

    /**
     * Menyimpan data user baru ke database.
     */
    public void saveUser(String nip, String passwordHash) {
        jdbc.update("INSERT INTO users (nip, password_hash) VALUES (?, ?)", nip, passwordHash);
    }

    /**
     * Menyimpan data refresh token baru.
     */
    public void saveRefreshToken(int userId, String tokenHash, Timestamp expiresAt) {
        jdbc.update(
                "INSERT INTO refresh_tokens (user_id, token_hash, expires_at) VALUES (?, ?, ?)",
                userId, tokenHash, expiresAt);
    }

    /**
     * Mencari NIP pemilik refresh token tertentu yang belum kadaluarsa.
     * 
     * PENJELASAN UNTUK MAHASISWA:
     * - AND rt.expires_at > NOW(): Memastikan refresh token hanya valid jika masa berlakunya 
     *   masih lebih besar dari waktu sekarang di database.
     */
    public Optional<String> findNipByRefreshToken(String hash) {
        try {
            String nip = jdbc.queryForObject(
                    """
                    SELECT u.nip FROM refresh_tokens rt
                    JOIN users u ON rt.user_id = u.id
                    WHERE rt.token_hash = ? AND rt.expires_at > NOW()
                    """,
                    String.class,
                    hash);
            return Optional.ofNullable(nip);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Menghapus refresh token tertentu dari database (digunakan pada rotasi token).
     */
    public void deleteRefreshToken(String hash) {
        jdbc.update("DELETE FROM refresh_tokens WHERE token_hash = ?", hash);
    }

    /**
     * Representasi Java Record untuk baris data tabel `users`.
     */
    record UserRecord(int id, String nip, String passwordHash) {}
}


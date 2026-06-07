package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.DropdownOption;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InputRepository {

    private final NamedParameterJdbcTemplate jdbc;

    // Helper for safely getting nullable Integer from ResultSet
    private Integer getInteger(ResultSet rs, String col) throws SQLException {
        int val = rs.getInt(col);
        return rs.wasNull() ? null : val;
    }

    // --- Master Data Find-or-Create Methods ---

    public Integer findOrCreateJenisAsn(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_jenis_asn FROM jenis_asn WHERE LOWER(TRIM(nama_jenis)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_jenis_asn"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO jenis_asn (nama_jenis) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_asn"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateKedudukanAsn(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_kedudukan FROM kedudukan_asn WHERE LOWER(TRIM(nama_kedudukan)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_kedudukan"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO kedudukan_asn (nama_kedudukan) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_kedudukan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateJenisKelamin(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_jenis_kelamin FROM jenis_kelamin WHERE LOWER(TRIM(nama_kelamin)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_jenis_kelamin"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO jenis_kelamin (nama_kelamin) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_kelamin"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateWilayahPokja(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_wilayah_pokja FROM wilayah_pokja WHERE LOWER(TRIM(nama_pokja)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_wilayah_pokja"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO wilayah_pokja (nama_pokja) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_wilayah_pokja"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateWilayahBkn(String nama, Integer idPokja, Integer noUrut) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_wilker, id_wilayah_pokja, no_urut FROM wilayah_bkn WHERE LOWER(TRIM(nama_wilker)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", trimNama)
                .addValue("idPokja", idPokja)
                .addValue("noUrut", noUrut);
        
        List<WilkerRecord> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> new WilkerRecord(
                rs.getInt("id_wilker"),
                getInteger(rs, "id_wilayah_pokja"),
                getInteger(rs, "no_urut")
        ));
        if (!list.isEmpty()) {
            WilkerRecord existing = list.get(0);
            boolean needUpdate = false;
            if (idPokja != null && !idPokja.equals(existing.idPokja())) needUpdate = true;
            if (noUrut != null && !noUrut.equals(existing.noUrut())) needUpdate = true;
            if (needUpdate) {
                jdbc.update("UPDATE wilayah_bkn SET id_wilayah_pokja = COALESCE(:idPokja, id_wilayah_pokja), no_urut = COALESCE(:noUrut, no_urut) WHERE id_wilker = :id",
                        new MapSqlParameterSource()
                                .addValue("idPokja", idPokja)
                                .addValue("noUrut", noUrut)
                                .addValue("id", existing.id()));
            }
            return existing.id();
        }
        String sqlInsert = "INSERT INTO wilayah_bkn (nama_wilker, id_wilayah_pokja, no_urut) VALUES (:nama, :idPokja, :noUrut)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_wilker"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateInstansi(String nama, String kategori, Integer idWilker) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_instansi, kategori, id_wilker FROM instansi WHERE LOWER(TRIM(nama_instansi)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", trimNama)
                .addValue("kategori", kategori)
                .addValue("idWilker", idWilker);

        List<InstansiRecord> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> new InstansiRecord(
                rs.getInt("id_instansi"),
                rs.getString("kategori"),
                getInteger(rs, "id_wilker")
        ));
        if (!list.isEmpty()) {
            InstansiRecord existing = list.get(0);
            boolean needUpdate = false;
            if (kategori != null && !kategori.equalsIgnoreCase(existing.kategori())) needUpdate = true;
            if (idWilker != null && !idWilker.equals(existing.idWilker())) needUpdate = true;
            if (needUpdate) {
                jdbc.update("UPDATE instansi SET kategori = COALESCE(:kategori, kategori), id_wilker = COALESCE(:idWilker, id_wilker) WHERE id_instansi = :id",
                        new MapSqlParameterSource()
                                .addValue("kategori", kategori)
                                .addValue("idWilker", idWilker)
                                .addValue("id", existing.id()));
            }
            return existing.id();
        }
        String sqlInsert = "INSERT INTO instansi (nama_instansi, kategori, id_wilker) VALUES (:nama, :kategori, :idWilker)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_instansi"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreatePendidikan(String nama, String tingkat) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_pendidikan, tingkat FROM pendidikan WHERE LOWER(TRIM(nama_pendidikan)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", trimNama)
                .addValue("tingkat", tingkat);

        List<PendidikanRecord> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> new PendidikanRecord(
                rs.getInt("id_pendidikan"),
                rs.getString("tingkat")
        ));
        if (!list.isEmpty()) {
            PendidikanRecord existing = list.get(0);
            if (tingkat != null && !tingkat.equalsIgnoreCase(existing.tingkat())) {
                jdbc.update("UPDATE pendidikan SET tingkat = :tingkat WHERE id_pendidikan = :id",
                        new MapSqlParameterSource("tingkat", tingkat).addValue("id", existing.id()));
            }
            return existing.id();
        }
        String sqlInsert = "INSERT INTO pendidikan (nama_pendidikan, tingkat) VALUES (:nama, :tingkat)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_pendidikan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateNomenklatur(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_nomenklatur FROM nomenklatur WHERE LOWER(TRIM(nama_nomenklatur)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_nomenklatur"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO nomenklatur (nama_nomenklatur) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_nomenklatur"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateJenisJf(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_jenis_jf FROM jenis_jf WHERE LOWER(TRIM(nama_jenis_jf)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_jenis_jf"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO jenis_jf (nama_jenis_jf) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_jf"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateJabatan(String nama, String jenjang, Integer idNomenklatur, Integer idJenisJf) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_jabatan, id_nomenklatur, id_jenis_jf FROM jabatan WHERE LOWER(TRIM(nama_jabatan)) = LOWER(TRIM(:nama)) AND (:jenjang IS NULL OR LOWER(TRIM(jenjang)) = LOWER(TRIM(:jenjang)))";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", trimNama)
                .addValue("jenjang", jenjang)
                .addValue("idNomenklatur", idNomenklatur)
                .addValue("idJenisJf", idJenisJf);

        List<JabatanRecord> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> new JabatanRecord(
                rs.getInt("id_jabatan"),
                getInteger(rs, "id_nomenklatur"),
                getInteger(rs, "id_jenis_jf")
        ));
        if (!list.isEmpty()) {
            JabatanRecord existing = list.get(0);
            boolean needUpdate = false;
            if (idNomenklatur != null && !idNomenklatur.equals(existing.idNomenklatur())) needUpdate = true;
            if (idJenisJf != null && !idJenisJf.equals(existing.idJenisJf())) needUpdate = true;
            if (needUpdate) {
                jdbc.update("UPDATE jabatan SET id_nomenklatur = COALESCE(:idNomenklatur, id_nomenklatur), id_jenis_jf = COALESCE(:idJenisJf, id_jenis_jf) WHERE id_jabatan = :id",
                        new MapSqlParameterSource()
                                .addValue("idNomenklatur", idNomenklatur)
                                .addValue("idJenisJf", idJenisJf)
                                .addValue("id", existing.id()));
            }
            return existing.id();
        }
        String sqlInsert = "INSERT INTO jabatan (nama_jabatan, jenjang, id_nomenklatur, id_jenis_jf) VALUES (:nama, :jenjang, :idNomenklatur, :idJenisJf)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jabatan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateGolongan(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_golongan FROM golongan WHERE LOWER(TRIM(golongan_ruang)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_golongan"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO golongan (golongan_ruang) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_golongan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer findOrCreateJenisDiklat(String nama) {
        if (nama == null || nama.trim().isEmpty()) return null;
        String trimNama = nama.trim();
        String sqlSelect = "SELECT id_jenis_diklat FROM jenis_diklat WHERE LOWER(TRIM(nama_jenis_diklat)) = LOWER(TRIM(:nama))";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", trimNama);
        List<Integer> list = jdbc.query(sqlSelect, params, (rs, rowNum) -> rs.getInt("id_jenis_diklat"));
        if (!list.isEmpty()) {
            return list.get(0);
        }
        String sqlInsert = "INSERT INTO jenis_diklat (nama_jenis_diklat) VALUES (:nama)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_diklat"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    // --- Record classes for update logic ---
    record WilkerRecord(int id, Integer idPokja, Integer noUrut) {}
    record InstansiRecord(int id, String kategori, Integer idWilker) {}
    record PendidikanRecord(int id, String tingkat) {}
    record JabatanRecord(int id, Integer idNomenklatur, Integer idJenisJf) {}

    // --- ASN CRUD Core Methods ---

    public void upsertAsn(
            Long idAsn, String nip, Integer idJenisAsn, Integer idKedudukan, Integer idJenisKelamin,
            Integer idPendidikan, Integer idInstansi, Integer idJabatan, Integer idGolongan,
            Integer idJenisDiklat, LocalDate tmtJabatan, Integer masaKerjaJabatan,
            LocalDate tmtGolongan, Integer masaKerjaGolongan
    ) {
        String sql = """
            INSERT INTO asn (
                id_asn, nip, id_jenis_asn, id_kedudukan, id_jenis_kelamin, id_pendidikan,
                id_instansi, id_jabatan, id_golongan, id_jenis_diklat,
                tmt_jabatan, masa_kerja_jabatan, tmt_golongan, masa_kerja_golongan
            ) VALUES (
                :idAsn, :nip, :idJenisAsn, :idKedudukan, :idJenisKelamin, :idPendidikan,
                :idInstansi, :idJabatan, :idGolongan, :idJenisDiklat,
                :tmtJabatan, :masaKerjaJabatan, :tmtGolongan, :masaKerjaGolongan
            )
            ON CONFLICT (id_asn) DO UPDATE SET
                nip = EXCLUDED.nip,
                id_jenis_asn = EXCLUDED.id_jenis_asn,
                id_kedudukan = EXCLUDED.id_kedudukan,
                id_jenis_kelamin = EXCLUDED.id_jenis_kelamin,
                id_pendidikan = EXCLUDED.id_pendidikan,
                id_instansi = EXCLUDED.id_instansi,
                id_jabatan = EXCLUDED.id_jabatan,
                id_golongan = EXCLUDED.id_golongan,
                id_jenis_diklat = EXCLUDED.id_jenis_diklat,
                tmt_jabatan = EXCLUDED.tmt_jabatan,
                masa_kerja_jabatan = EXCLUDED.masa_kerja_jabatan,
                tmt_golongan = EXCLUDED.tmt_golongan,
                masa_kerja_golongan = EXCLUDED.masa_kerja_golongan
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idAsn", idAsn)
                .addValue("nip", nip)
                .addValue("idJenisAsn", idJenisAsn)
                .addValue("idKedudukan", idKedudukan)
                .addValue("idJenisKelamin", idJenisKelamin)
                .addValue("idPendidikan", idPendidikan)
                .addValue("idInstansi", idInstansi)
                .addValue("idJabatan", idJabatan)
                .addValue("idGolongan", idGolongan)
                .addValue("idJenisDiklat", idJenisDiklat)
                .addValue("tmtJabatan", tmtJabatan)
                .addValue("masaKerjaJabatan", masaKerjaJabatan)
                .addValue("tmtGolongan", tmtGolongan)
                .addValue("masaKerjaGolongan", masaKerjaGolongan);

        jdbc.update(sql, params);
    }

    public Optional<InputResponse> findById(Long idAsn) {
        String sql = """
            SELECT 
                a.id_asn,
                a.nip,
                ja.nama_jenis AS jenis_asn,
                ka.nama_kedudukan AS kedudukan_asn,
                jk.nama_kelamin AS jenis_kelamin,
                i.nama_instansi AS instansi_kerja,
                i.kategori AS kategori_instansi,
                p.tingkat AS tingkat_pendidikan,
                p.nama_pendidikan AS pendidikan,
                j.nama_jabatan AS nama_jabatan,
                j.jenjang AS jenjang,
                jf.nama_jenis_jf AS jenis_jf,
                n.nama_nomenklatur AS nomenklatur,
                g.golongan_ruang AS golongan,
                jd.nama_jenis_diklat AS jenis_diklat,
                a.tmt_jabatan,
                a.tmt_golongan AS tmt_golru,
                wb.nama_wilker AS wilker_bkn,
                wb.no_urut AS no_urut_wilker,
                wp.nama_pokja AS wilayah_pokja,
                a.masa_kerja_golongan AS mk_golongan,
                a.masa_kerja_jabatan AS mk_jabatan
            FROM asn a
            LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
            LEFT JOIN kedudukan_asn ka ON a.id_kedudukan = ka.id_kedudukan
            LEFT JOIN jenis_kelamin jk ON a.id_jenis_kelamin = jk.id_jenis_kelamin
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn wb ON i.id_wilker = wb.id_wilker
            LEFT JOIN wilayah_pokja wp ON wb.id_wilayah_pokja = wp.id_wilayah_pokja
            LEFT JOIN pendidikan p ON a.id_pendidikan = p.id_pendidikan
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN jenis_jf jf ON j.id_jenis_jf = jf.id_jenis_jf
            LEFT JOIN nomenklatur n ON j.id_nomenklatur = n.id_nomenklatur
            LEFT JOIN golongan g ON a.id_golongan = g.id_golongan
            LEFT JOIN jenis_diklat jd ON a.id_jenis_diklat = jd.id_jenis_diklat
            WHERE a.id_asn = :idAsn
            """;
        try {
            InputResponse response = jdbc.queryForObject(sql, new MapSqlParameterSource("idAsn", idAsn), (rs, rowNum) -> mapRowToResponse(rs));
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean deleteById(Long idAsn) {
        String sql = "DELETE FROM asn WHERE id_asn = :idAsn";
        int rows = jdbc.update(sql, new MapSqlParameterSource("idAsn", idAsn));
        return rows > 0;
    }

    public List<InputResponse> fetchPaginated(String search, int limit, int offset) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                a.id_asn,
                a.nip,
                ja.nama_jenis AS jenis_asn,
                ka.nama_kedudukan AS kedudukan_asn,
                jk.nama_kelamin AS jenis_kelamin,
                i.nama_instansi AS instansi_kerja,
                i.kategori AS kategori_instansi,
                p.tingkat AS tingkat_pendidikan,
                p.nama_pendidikan AS pendidikan,
                j.nama_jabatan AS nama_jabatan,
                j.jenjang AS jenjang,
                jf.nama_jenis_jf AS jenis_jf,
                n.nama_nomenklatur AS nomenklatur,
                g.golongan_ruang AS golongan,
                jd.nama_jenis_diklat AS jenis_diklat,
                a.tmt_jabatan,
                a.tmt_golongan AS tmt_golru,
                wb.nama_wilker AS wilker_bkn,
                wb.no_urut AS no_urut_wilker,
                wp.nama_pokja AS wilayah_pokja,
                a.masa_kerja_golongan AS mk_golongan,
                a.masa_kerja_jabatan AS mk_jabatan
            FROM asn a
            LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
            LEFT JOIN kedudukan_asn ka ON a.id_kedudukan = ka.id_kedudukan
            LEFT JOIN jenis_kelamin jk ON a.id_jenis_kelamin = jk.id_jenis_kelamin
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn wb ON i.id_wilker = wb.id_wilker
            LEFT JOIN wilayah_pokja wp ON wb.id_wilayah_pokja = wp.id_wilayah_pokja
            LEFT JOIN pendidikan p ON a.id_pendidikan = p.id_pendidikan
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN jenis_jf jf ON j.id_jenis_jf = jf.id_jenis_jf
            LEFT JOIN nomenklatur n ON j.id_nomenklatur = n.id_nomenklatur
            LEFT JOIN golongan g ON a.id_golongan = g.id_golongan
            LEFT JOIN jenis_diklat jd ON a.id_jenis_diklat = jd.id_jenis_diklat
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (CAST(a.id_asn AS VARCHAR) LIKE :search OR LOWER(j.nama_jabatan) LIKE :search OR LOWER(i.nama_instansi) LIKE :search OR LOWER(n.nama_nomenklatur) LIKE :search)");
            params.addValue("search", "%" + search.trim().toLowerCase() + "%");
        }

        sql.append(" ORDER BY a.id_asn ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> mapRowToResponse(rs));
    }

    public long countTotal(String search) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) 
            FROM asn a
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN nomenklatur n ON j.id_nomenklatur = n.id_nomenklatur
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (CAST(a.id_asn AS VARCHAR) LIKE :search OR LOWER(j.nama_jabatan) LIKE :search OR LOWER(i.nama_instansi) LIKE :search OR LOWER(n.nama_nomenklatur) LIKE :search)");
            params.addValue("search", "%" + search.trim().toLowerCase() + "%");
        }

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    // Map query row to InputResponse DTO
    private InputResponse mapRowToResponse(ResultSet rs) throws SQLException {
        java.sql.Date tmtJab = rs.getDate("tmt_jabatan");
        java.sql.Date tmtGol = rs.getDate("tmt_golru");
        Integer mkGol = getInteger(rs, "mk_golongan");
        Integer mkJab = getInteger(rs, "mk_jabatan");

        return InputResponse.builder()
                .idAsn(rs.getLong("id_asn"))
                .nip(rs.getString("nip"))
                .jenisAsn(rs.getString("jenis_asn"))
                .kedudukanAsn(rs.getString("kedudukan_asn"))
                .jenisKelamin(rs.getString("jenis_kelamin"))
                .instansiKerja(rs.getString("instansi_kerja"))
                .kategoriInstansi(rs.getString("kategori_instansi"))
                .unitKerja(null) // Excel-only / ignored
                .tingkatPendidikan(rs.getString("tingkat_pendidikan"))
                .pendidikan(rs.getString("pendidikan"))
                .jabatan(rs.getString("nama_jabatan")) // maps 'jabatan' to nama_jabatan description
                .noUrutJenjang(null) // Excel-only / ignored
                .jenjang(rs.getString("jenjang"))
                .jenisJf(rs.getString("jenis_jf"))
                .namaJabatan(rs.getString("nama_jabatan"))
                .nomenklatur(rs.getString("nomenklatur"))
                .golongan(rs.getString("golongan"))
                .jenisDiklat(rs.getString("jenis_diklat"))
                .tmtJabatan(tmtJab != null ? tmtJab.toLocalDate() : null)
                .masaKerjaJabatanString(mkJab != null ? mkJab + " Tahun" : null)
                .golonganRuang(rs.getString("golongan"))
                .tmtGolru(tmtGol != null ? tmtGol.toLocalDate() : null)
                .masaKerjaGolonganString(mkGol != null ? mkGol + " Tahun" : null)
                .wilkerBkn(rs.getString("wilker_bkn"))
                .noUrutWilker(getInteger(rs, "no_urut_wilker"))
                .wilayahPokja(rs.getString("wilayah_pokja"))
                .mkGolongan(mkGol)
                .mkJabatan(mkJab)
                .build();
    }

    public List<DropdownOption> getJenisAsnOptions() {
        return jdbc.query("SELECT DISTINCT nama_jenis AS label, nama_jenis AS value FROM jenis_asn WHERE nama_jenis IS NOT NULL AND TRIM(nama_jenis) != '' ORDER BY nama_jenis", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getKedudukanAsnOptions() {
        return jdbc.query("SELECT DISTINCT nama_kedudukan AS label, nama_kedudukan AS value FROM kedudukan_asn WHERE nama_kedudukan IS NOT NULL AND TRIM(nama_kedudukan) != '' ORDER BY nama_kedudukan", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJenisKelaminOptions() {
        return jdbc.query("SELECT DISTINCT nama_kelamin AS label, nama_kelamin AS value FROM jenis_kelamin WHERE nama_kelamin IS NOT NULL AND TRIM(nama_kelamin) != '' ORDER BY nama_kelamin", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getInstansiKerjaOptions() {
        return jdbc.query("SELECT DISTINCT nama_instansi AS label, nama_instansi AS value FROM instansi WHERE nama_instansi IS NOT NULL AND TRIM(nama_instansi) != '' ORDER BY nama_instansi", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getKategoriInstansiOptions() {
        return jdbc.query("SELECT DISTINCT kategori AS label, kategori AS value FROM instansi WHERE kategori IS NOT NULL AND TRIM(kategori) != '' ORDER BY kategori", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJenisInstansiOptions() {
        return jdbc.query("SELECT DISTINCT jenis_instansi AS label, jenis_instansi AS value FROM instansi WHERE jenis_instansi IS NOT NULL AND TRIM(jenis_instansi) != '' ORDER BY jenis_instansi", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getTingkatPendidikanOptions() {
        return jdbc.query("SELECT DISTINCT tingkat AS label, tingkat AS value FROM pendidikan WHERE tingkat IS NOT NULL AND TRIM(tingkat) != '' ORDER BY tingkat", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getPendidikanOptions() {
        return jdbc.query("SELECT DISTINCT nama_pendidikan AS label, nama_pendidikan AS value FROM pendidikan WHERE nama_pendidikan IS NOT NULL AND TRIM(nama_pendidikan) != '' ORDER BY nama_pendidikan", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJabatanOptions() {
        return jdbc.query("SELECT DISTINCT nama_jabatan AS label, nama_jabatan AS value FROM jabatan WHERE nama_jabatan IS NOT NULL AND TRIM(nama_jabatan) != '' ORDER BY nama_jabatan", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJenjangOptions() {
        return jdbc.query("SELECT DISTINCT jenjang AS label, jenjang AS value FROM jabatan WHERE jenjang IS NOT NULL AND TRIM(jenjang) != '' ORDER BY jenjang", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJenisJfOptions() {
        return jdbc.query("SELECT DISTINCT nama_jenis_jf AS label, nama_jenis_jf AS value FROM jenis_jf WHERE nama_jenis_jf IS NOT NULL AND TRIM(nama_jenis_jf) != '' ORDER BY nama_jenis_jf", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getNomenklaturOptions() {
        return jdbc.query("SELECT DISTINCT nama_nomenklatur AS label, nama_nomenklatur AS value FROM nomenklatur WHERE nama_nomenklatur IS NOT NULL AND TRIM(nama_nomenklatur) != '' ORDER BY nama_nomenklatur", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getGolonganOptions() {
        return jdbc.query("SELECT DISTINCT golongan_ruang AS label, golongan_ruang AS value FROM golongan WHERE golongan_ruang IS NOT NULL AND TRIM(golongan_ruang) != '' ORDER BY golongan_ruang", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getJenisDiklatOptions() {
        return jdbc.query("SELECT DISTINCT nama_jenis_diklat AS label, nama_jenis_diklat AS value FROM jenis_diklat WHERE nama_jenis_diklat IS NOT NULL AND TRIM(nama_jenis_diklat) != '' ORDER BY nama_jenis_diklat", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getWilkerBknOptions() {
        return jdbc.query("SELECT DISTINCT nama_wilker AS label, nama_wilker AS value FROM wilayah_bkn WHERE nama_wilker IS NOT NULL AND TRIM(nama_wilker) != '' ORDER BY nama_wilker", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
    public List<DropdownOption> getWilayahPokjaOptions() {
        return jdbc.query("SELECT DISTINCT nama_pokja AS label, nama_pokja AS value FROM wilayah_pokja WHERE nama_pokja IS NOT NULL AND TRIM(nama_pokja) != '' ORDER BY nama_pokja", 
            (rs, rowNum) -> new DropdownOption(rs.getString("label"), rs.getString("value")));
    }
}

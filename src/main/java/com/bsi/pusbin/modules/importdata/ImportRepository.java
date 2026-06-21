package com.bsi.pusbin.modules.importdata;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ImportRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public record AsnRecord(
            String nip,
            String nama,
            Integer idJenisAsn,
            Integer idKedudukan,
            Integer idJenisKelamin,
            Integer idPendidikan,
            Integer idInstansi,
            Integer idJabatan,
            Integer idGolongan,
            Integer idJenisDiklat,
            LocalDate tmtJabatan,
            Integer masaKerjaJabatan,
            LocalDate tmtGolongan,
            Integer masaKerjaGolongan
    ) {}

    // --- Cache Loading Methods ---

    public Map<String, Integer> getJenisAsnMap() {
        String sql = "SELECT id_jenis_asn, nama_jenis FROM jenis_asn WHERE nama_jenis IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_jenis");
            Integer id = ((Number) r.get("id_jenis_asn")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getKedudukanAsnMap() {
        String sql = "SELECT id_kedudukan, nama_kedudukan FROM kedudukan_asn WHERE nama_kedudukan IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_kedudukan");
            Integer id = ((Number) r.get("id_kedudukan")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getJenisKelaminMap() {
        String sql = "SELECT id_jenis_kelamin, nama_kelamin FROM jenis_kelamin WHERE nama_kelamin IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_kelamin");
            Integer id = ((Number) r.get("id_jenis_kelamin")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getWilayahPokjaMap() {
        String sql = "SELECT id_wilayah_pokja, nama_pokja FROM wilayah_pokja WHERE nama_pokja IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_pokja");
            Integer id = ((Number) r.get("id_wilayah_pokja")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getWilayahBknMap() {
        String sql = "SELECT id_wilker, nama_wilker FROM wilayah_bkn WHERE nama_wilker IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_wilker");
            Integer id = ((Number) r.get("id_wilker")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getInstansiMap() {
        String sql = "SELECT id_instansi, nama_instansi FROM instansi WHERE nama_instansi IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_instansi");
            Integer id = ((Number) r.get("id_instansi")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getPendidikanMap() {
        String sql = "SELECT id_pendidikan, nama_pendidikan FROM pendidikan WHERE nama_pendidikan IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_pendidikan");
            Integer id = ((Number) r.get("id_pendidikan")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getNomenklaturMap() {
        String sql = "SELECT id_nomenklatur, nama_nomenklatur FROM nomenklatur WHERE nama_nomenklatur IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_nomenklatur");
            Integer id = ((Number) r.get("id_nomenklatur")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getJenisJfMap() {
        String sql = "SELECT id_jenis_jf, nama_jenis_jf FROM jenis_jf WHERE nama_jenis_jf IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_jenis_jf");
            Integer id = ((Number) r.get("id_jenis_jf")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getJabatanMap() {
        String sql = "SELECT id_jabatan, nama_jabatan, jenjang, id_nomenklatur, id_jenis_jf FROM jabatan WHERE nama_jabatan IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_jabatan");
            String jenjang = (String) r.get("jenjang");
            Number idNomObj = (Number) r.get("id_nomenklatur");
            Number idJfObj = (Number) r.get("id_jenis_jf");
            String idNom = idNomObj != null ? String.valueOf(idNomObj.intValue()) : "null";
            String idJf = idJfObj != null ? String.valueOf(idJfObj.intValue()) : "null";
            
            String key = name.trim().toLowerCase() + "||" + (jenjang == null ? "" : jenjang.trim().toLowerCase()) + "||" + idNom + "||" + idJf;
            Integer id = ((Number) r.get("id_jabatan")).intValue();
            map.put(key, id);
        }
        return map;
    }

    public Map<String, Integer> getGolonganMap() {
        String sql = "SELECT id_golongan, golongan_ruang FROM golongan WHERE golongan_ruang IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("golongan_ruang");
            Integer id = ((Number) r.get("id_golongan")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    public Map<String, Integer> getJenisDiklatMap() {
        String sql = "SELECT id_jenis_diklat, nama_jenis_diklat FROM jenis_diklat WHERE nama_jenis_diklat IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_jenis_diklat");
            Integer id = ((Number) r.get("id_jenis_diklat")).intValue();
            map.put(name.trim().toLowerCase(), id);
        }
        return map;
    }

    // --- On-the-fly Master Insert Methods ---

    public Integer getOrInsertJenisAsn(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_jenis_asn FROM jenis_asn WHERE nama_jenis ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO jenis_asn (nama_jenis) VALUES (:nama)", params, keyHolder, new String[]{"id_jenis_asn"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertKedudukanAsn(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_kedudukan FROM kedudukan_asn WHERE nama_kedudukan ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO kedudukan_asn (nama_kedudukan) VALUES (:nama)", params, keyHolder, new String[]{"id_kedudukan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertJenisKelamin(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_jenis_kelamin FROM jenis_kelamin WHERE nama_kelamin ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO jenis_kelamin (nama_kelamin) VALUES (:nama)", params, keyHolder, new String[]{"id_jenis_kelamin"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertWilayahPokja(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_wilayah_pokja FROM wilayah_pokja WHERE nama_pokja ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO wilayah_pokja (nama_pokja) VALUES (:nama)", params, keyHolder, new String[]{"id_wilayah_pokja"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertWilayahBkn(String nama, Integer idPokja, Integer noUrut) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("idPokja", idPokja)
                .addValue("noUrut", noUrut);
        List<Integer> ids = jdbc.queryForList("SELECT id_wilker FROM wilayah_bkn WHERE nama_wilker ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO wilayah_bkn (nama_wilker, id_wilayah_pokja, no_urut) VALUES (:nama, :idPokja, :noUrut)", params, keyHolder, new String[]{"id_wilker"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertInstansi(String nama, String kategori, String jenisInstansi, Integer idWilker) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("kategori", kategori != null ? kategori.trim() : null)
                .addValue("jenisInstansi", jenisInstansi != null ? jenisInstansi.trim() : null)
                .addValue("idWilker", idWilker);
        List<Integer> ids = jdbc.queryForList("SELECT id_instansi FROM instansi WHERE nama_instansi ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) {
            Integer existingId = ids.get(0);
            // Update kategori, jenis_instansi and id_wilker on the existing record
            MapSqlParameterSource updateParams = new MapSqlParameterSource()
                    .addValue("id", existingId)
                    .addValue("kategori", kategori != null ? kategori.trim() : null)
                    .addValue("jenisInstansi", jenisInstansi != null ? jenisInstansi.trim() : null)
                    .addValue("idWilker", idWilker);
            jdbc.update("UPDATE instansi SET kategori = COALESCE(:kategori, kategori), jenis_instansi = COALESCE(:jenisInstansi, jenis_instansi), id_wilker = COALESCE(:idWilker, id_wilker) WHERE id_instansi = :id", updateParams);
            return existingId;
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO instansi (nama_instansi, kategori, jenis_instansi, id_wilker) VALUES (:nama, :kategori, :jenisInstansi, :idWilker)", params, keyHolder, new String[]{"id_instansi"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertPendidikan(String nama, String tingkat) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("tingkat", tingkat != null ? tingkat.trim() : null);
        List<Integer> ids = jdbc.queryForList("SELECT id_pendidikan FROM pendidikan WHERE nama_pendidikan ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO pendidikan (nama_pendidikan, tingkat) VALUES (:nama, :tingkat)", params, keyHolder, new String[]{"id_pendidikan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertNomenklatur(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_nomenklatur FROM nomenklatur WHERE nama_nomenklatur ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO nomenklatur (nama_nomenklatur) VALUES (:nama)", params, keyHolder, new String[]{"id_nomenklatur"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertJenisJf(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_jenis_jf FROM jenis_jf WHERE nama_jenis_jf ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO jenis_jf (nama_jenis_jf) VALUES (:nama)", params, keyHolder, new String[]{"id_jenis_jf"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertJabatan(String nama, String jenjang, Integer idNomenklatur, Integer idJenisJf) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("jenjang", jenjang != null ? jenjang.trim() : null)
                .addValue("idNomenklatur", idNomenklatur)
                .addValue("idJenisJf", idJenisJf);
        
        String sqlSelect = "SELECT id_jabatan FROM jabatan WHERE nama_jabatan ILIKE :nama ";
        if (jenjang != null && !jenjang.trim().isEmpty()) {
            sqlSelect += "AND jenjang ILIKE :jenjang ";
        } else {
            sqlSelect += "AND (jenjang IS NULL OR jenjang = '') ";
        }
        if (idNomenklatur != null) {
            sqlSelect += "AND id_nomenklatur = :idNomenklatur ";
        } else {
            sqlSelect += "AND id_nomenklatur IS NULL ";
        }
        if (idJenisJf != null) {
            sqlSelect += "AND id_jenis_jf = :idJenisJf ";
        } else {
            sqlSelect += "AND id_jenis_jf IS NULL ";
        }
        sqlSelect += "LIMIT 1";

        List<Integer> ids = jdbc.queryForList(sqlSelect, params, Integer.class);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO jabatan (nama_jabatan, jenjang, id_nomenklatur, id_jenis_jf) VALUES (:nama, :jenjang, :idNomenklatur, :idJenisJf)", params, keyHolder, new String[]{"id_jabatan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertGolongan(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_golongan FROM golongan WHERE golongan_ruang ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO golongan (golongan_ruang) VALUES (:nama)", params, keyHolder, new String[]{"id_golongan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer getOrInsertJenisDiklat(String nama) {
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        List<Integer> ids = jdbc.queryForList("SELECT id_jenis_diklat FROM jenis_diklat WHERE nama_jenis_diklat ILIKE :nama LIMIT 1", params, Integer.class);
        if (!ids.isEmpty()) return ids.get(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO jenis_diklat (nama_jenis_diklat) VALUES (:nama)", params, keyHolder, new String[]{"id_jenis_diklat"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    // --- Bulk Upsert Fact Table ---

    public void batchUpsertAsn(List<AsnRecord> records) {
        if (records.isEmpty()) return;
        String sql = """
            INSERT INTO asn (
                nip, nama, id_jenis_asn, id_kedudukan, id_jenis_kelamin, id_pendidikan,
                id_instansi, id_jabatan, id_golongan, id_jenis_diklat,
                tmt_jabatan, masa_kerja_jabatan, tmt_golongan, masa_kerja_golongan, updated_at
            ) VALUES (
                :nip, :nama, :idJenisAsn, :idKedudukan, :idJenisKelamin, :idPendidikan,
                :idInstansi, :idJabatan, :idGolongan, :idJenisDiklat,
                :tmtJabatan, :masaKerjaJabatan, :tmtGolongan, :masaKerjaGolongan, CURRENT_TIMESTAMP
            )
            ON CONFLICT (nip) DO UPDATE SET
                nama = EXCLUDED.nama,
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
                masa_kerja_golongan = EXCLUDED.masa_kerja_golongan,
                updated_at = CURRENT_TIMESTAMP
            """;

        MapSqlParameterSource[] batchParams = new MapSqlParameterSource[records.size()];
        for (int i = 0; i < records.size(); i++) {
            AsnRecord r = records.get(i);
            batchParams[i] = new MapSqlParameterSource()
                    .addValue("nip", r.nip())
                    .addValue("nama", r.nama())
                    .addValue("idJenisAsn", r.idJenisAsn())
                    .addValue("idKedudukan", r.idKedudukan())
                    .addValue("idJenisKelamin", r.idJenisKelamin())
                    .addValue("idPendidikan", r.idPendidikan())
                    .addValue("idInstansi", r.idInstansi())
                    .addValue("idJabatan", r.idJabatan())
                    .addValue("idGolongan", r.idGolongan())
                    .addValue("idJenisDiklat", r.idJenisDiklat())
                    .addValue("tmtJabatan", r.tmtJabatan())
                    .addValue("masaKerjaJabatan", r.masaKerjaJabatan())
                    .addValue("tmtGolongan", r.tmtGolongan())
                    .addValue("masaKerjaGolongan", r.masaKerjaGolongan());
        }

        jdbc.batchUpdate(sql, batchParams);
    }
}

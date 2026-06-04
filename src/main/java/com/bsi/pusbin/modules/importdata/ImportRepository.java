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
            Long idAsn,
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
        String sql = "SELECT id_jabatan, nama_jabatan, jenjang FROM jabatan WHERE nama_jabatan IS NOT NULL";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource());
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : rows) {
            String name = (String) r.get("nama_jabatan");
            String jenjang = (String) r.get("jenjang");
            String key = name.trim().toLowerCase() + "||" + (jenjang == null ? "" : jenjang.trim().toLowerCase());
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

    public Integer insertJenisAsn(String nama) {
        String sqlInsert = "INSERT INTO jenis_asn (nama_jenis) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_asn"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertKedudukanAsn(String nama) {
        String sqlInsert = "INSERT INTO kedudukan_asn (nama_kedudukan) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_kedudukan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertJenisKelamin(String nama) {
        String sqlInsert = "INSERT INTO jenis_kelamin (nama_kelamin) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_kelamin"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertWilayahPokja(String nama) {
        String sqlInsert = "INSERT INTO wilayah_pokja (nama_pokja) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_wilayah_pokja"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertWilayahBkn(String nama, Integer idPokja, Integer noUrut) {
        String sqlInsert = "INSERT INTO wilayah_bkn (nama_wilker, id_wilayah_pokja, no_urut) VALUES (:nama, :idPokja, :noUrut)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("idPokja", idPokja)
                .addValue("noUrut", noUrut);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_wilker"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertInstansi(String nama, String kategori, Integer idWilker) {
        String sqlInsert = "INSERT INTO instansi (nama_instansi, kategori, id_wilker) VALUES (:nama, :kategori, :idWilker)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("kategori", kategori != null ? kategori.trim() : null)
                .addValue("idWilker", idWilker);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_instansi"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertPendidikan(String nama, String tingkat) {
        String sqlInsert = "INSERT INTO pendidikan (nama_pendidikan, tingkat) VALUES (:nama, :tingkat)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("tingkat", tingkat != null ? tingkat.trim() : null);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_pendidikan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertNomenklatur(String nama) {
        String sqlInsert = "INSERT INTO nomenklatur (nama_nomenklatur) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_nomenklatur"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertJenisJf(String nama) {
        String sqlInsert = "INSERT INTO jenis_jf (nama_jenis_jf) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_jf"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertJabatan(String nama, String jenjang, Integer idNomenklatur, Integer idJenisJf) {
        String sqlInsert = "INSERT INTO jabatan (nama_jabatan, jenjang, id_nomenklatur, id_jenis_jf) VALUES (:nama, :jenjang, :idNomenklatur, :idJenisJf)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nama", nama.trim())
                .addValue("jenjang", jenjang != null ? jenjang.trim() : null)
                .addValue("idNomenklatur", idNomenklatur)
                .addValue("idJenisJf", idJenisJf);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jabatan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertGolongan(String nama) {
        String sqlInsert = "INSERT INTO golongan (golongan_ruang) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_golongan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public Integer insertJenisDiklat(String nama) {
        String sqlInsert = "INSERT INTO jenis_diklat (nama_jenis_diklat) VALUES (:nama)";
        MapSqlParameterSource params = new MapSqlParameterSource("nama", nama.trim());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder, new String[]{"id_jenis_diklat"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    // --- Bulk Upsert Fact Table ---

    public void batchUpsertAsn(List<AsnRecord> records) {
        if (records.isEmpty()) return;
        String sql = """
            INSERT INTO asn (
                id_asn, id_jenis_asn, id_kedudukan, id_jenis_kelamin, id_pendidikan,
                id_instansi, id_jabatan, id_golongan, id_jenis_diklat,
                tmt_jabatan, masa_kerja_jabatan, tmt_golongan, masa_kerja_golongan
            ) VALUES (
                :idAsn, :idJenisAsn, :idKedudukan, :idJenisKelamin, :idPendidikan,
                :idInstansi, :idJabatan, :idGolongan, :idJenisDiklat,
                :tmtJabatan, :masaKerjaJabatan, :tmtGolongan, :masaKerjaGolongan
            )
            ON CONFLICT (id_asn) DO UPDATE SET
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

        MapSqlParameterSource[] batchParams = new MapSqlParameterSource[records.size()];
        for (int i = 0; i < records.size(); i++) {
            AsnRecord r = records.get(i);
            batchParams[i] = new MapSqlParameterSource()
                    .addValue("idAsn", r.idAsn())
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

package com.bsi.pusbin.modules.grafik;

import com.bsi.pusbin.modules.grafik.schema.RawChartRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GrafikRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private void appendFilters(StringBuilder sql, MapSqlParameterSource params,
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {
        if (instansiId != null && !instansiId.isEmpty()) {
            sql.append(" AND a.id_instansi IN (:instansiId)");
            params.addValue("instansiId", instansiId);
        }
        if (jenisAsnId != null && !jenisAsnId.isEmpty()) {
            sql.append(" AND a.id_jenis_asn IN (:jenisAsnId)");
            params.addValue("jenisAsnId", jenisAsnId);
        }
        if (nomenklaturId != null && !nomenklaturId.isEmpty()) {
            sql.append(" AND j.id_nomenklatur IN (:nomenklaturId)");
            params.addValue("nomenklaturId", nomenklaturId);
        }
        if (jenjang != null && !jenjang.isEmpty()) {
            sql.append(" AND j.jenjang IN (:jenjang)");
            params.addValue("jenjang", jenjang);
        }
        if (kategori != null && !kategori.isEmpty()) {
            sql.append(" AND i.kategori IN (:kategori)");
            params.addValue("kategori", kategori);
        }
        if (wilayahPokjaId != null && !wilayahPokjaId.isEmpty()) {
            sql.append(" AND w.id_wilayah_pokja IN (:wilayahPokjaId)");
            params.addValue("wilayahPokjaId", wilayahPokjaId);
        }
        if (namaJabatanId != null && !namaJabatanId.isEmpty()) {
            sql.append(" AND a.id_jabatan IN (:namaJabatanId)");
            params.addValue("namaJabatanId", namaJabatanId);
        }
        if (jenisInstansi != null && !jenisInstansi.isEmpty()) {
            sql.append(" AND i.jenis_instansi IN (:jenisInstansi)");
            params.addValue("jenisInstansi", jenisInstansi);
        }
        if (jenisKelaminId != null && !jenisKelaminId.isEmpty()) {
            sql.append(" AND a.id_jenis_kelamin IN (:jenisKelaminId)");
            params.addValue("jenisKelaminId", jenisKelaminId);
        }
        if (golonganId != null && !golonganId.isEmpty()) {
            sql.append(" AND a.id_golongan IN (:golonganId)");
            params.addValue("golonganId", golonganId);
        }
        if (pendidikanId != null && !pendidikanId.isEmpty()) {
            sql.append(" AND a.id_pendidikan IN (:pendidikanId)");
            params.addValue("pendidikanId", pendidikanId);
        }
        if (masaKerjaGolongan != null && !masaKerjaGolongan.isEmpty()) {
            sql.append(" AND (");

            for (int i = 0; i < masaKerjaGolongan.size(); i++) {
                if (i > 0)
                    sql.append(" OR ");

                String val = masaKerjaGolongan.get(i).replaceAll("\\s+", "");

                if (val.contains("<5")) {
                    sql.append("a.masa_kerja_golongan < 5");
                } else if (val.contains(">=5")) {
                    sql.append("a.masa_kerja_golongan >= 5");
                }
            }

            sql.append(")");
        }
        if (masaKerjaJabatan != null && !masaKerjaJabatan.isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < masaKerjaJabatan.size(); i++) {
                if (i > 0)
                    sql.append(" OR ");
                String val = masaKerjaJabatan.get(i).replaceAll("\\s+", "");
                if (val.contains("<9")) {
                    sql.append("a.masa_kerja_jabatan < 9");
                } else if (val.contains(">=9")) {
                    sql.append("a.masa_kerja_jabatan >= 9");
                }
            }
            sql.append(")");
        }
        if (kategoriJf != null && !kategoriJf.isEmpty()) {
            sql.append(
                    " AND EXISTS (SELECT 1 FROM jenis_jf jj WHERE j.id_jenis_jf = jj.id_jenis_jf AND jj.nama_jenis_jf IN (:kategoriJf))");
            params.addValue("kategoriJf", kategoriJf);
        }
    }



    public List<RawChartRow> getSebaranAsnJenjang(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(j.jenjang, 'Tanpa Jenjang') AS category,
                        COALESCE(ja.nama_jenis, 'Tanpa Jenis') AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY j.jenjang, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(rs.getString("category"),
                rs.getString("seriesLabel"), rs.getLong("value")));
    }

    public List<RawChartRow> getPersentaseGender(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(jk.nama_kelamin, 'Tanpa Gender') AS category,
                        NULL AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jenis_kelamin jk ON a.id_jenis_kelamin = jk.id_jenis_kelamin
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY jk.nama_kelamin");

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }

    public List<RawChartRow> getPersentaseAsnJfMasn(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(ja.nama_jenis, 'Tanpa Jenis') AS category,
                        NULL AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY ja.nama_jenis");

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }

    public List<RawChartRow> getSebaranAsnJfmasnInstansi(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(NULLIF(TRIM(i.jenis_instansi), ''), 'Tanpa Jenis Instansi') AS category,
                        COALESCE(NULLIF(TRIM(ja.nama_jenis), ''), 'Tanpa Jenis') AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY i.jenis_instansi, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(rs.getString("category"),
                rs.getString("seriesLabel"), rs.getLong("value")));
    }

    public List<RawChartRow> getSebaranAsnKlpd(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(i.kategori, 'Tanpa Kategori') AS category,
                        COALESCE(ja.nama_jenis, 'Tanpa Jenis') AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY i.kategori, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(rs.getString("category"),
                rs.getString("seriesLabel"), rs.getLong("value")));
    }

    public List<RawChartRow> getSebaranAsnJabatan(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(j.nama_jabatan, 'Tanpa Jabatan') AS category,
                        COALESCE(ja.nama_jenis, 'Tanpa Jenis') AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY j.nama_jabatan, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(rs.getString("category"),
                rs.getString("seriesLabel"), rs.getLong("value")));
    }

    public List<RawChartRow> getTrenKenaikanJf(List<Integer> namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        TO_CHAR(t.periode, 'DD Mon YYYY') AS category,
                        COALESCE(j.nama_jabatan, 'Tanpa Jabatan') AS seriesLabel,
                        t.jumlah_asn AS value
                    FROM total_asn_periode_by_nama_jabatan t
                    LEFT JOIN jabatan j
                        ON t.id_jabatan = j.id_jabatan
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (namaJabatanId != null && !namaJabatanId.isEmpty()) {
            sql.append(" AND t.id_jabatan IN (:namaJabatanId)");
            params.addValue("namaJabatanId", namaJabatanId);
        }

        sql.append("""
                    ORDER BY t.periode ASC
                """);

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(
                rs.getString("category"),
                rs.getString("seriesLabel"),
                rs.getLong("value")));
    }

    public List<RawChartRow> getGolonganRuang(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(g.golongan_ruang, 'Tanpa Golongan') AS category,
                        NULL AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN golongan g ON a.id_golongan = g.id_golongan
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY g.golongan_ruang");

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }

    public List<RawChartRow> getPersentaseJfMasn(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(j.nama_jabatan, 'Tanpa Jabatan') AS category,
                        NULL AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY j.nama_jabatan");

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }

    public List<RawChartRow> getSebaranKategori(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        COALESCE(NULLIF(TRIM(jj.nama_jenis_jf), ''), 'Tanpa Kategori') AS category,
                        COALESCE(NULLIF(TRIM(ja.nama_jenis), ''), 'Tanpa Jenis') AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN jenis_jf jj ON j.id_jenis_jf = jj.id_jenis_jf
                    LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY jj.nama_jenis_jf, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new RawChartRow(rs.getString("category"),
                rs.getString("seriesLabel"), rs.getLong("value")));
    }

    public List<RawChartRow> getMasaKerjaJabatan(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        CASE WHEN a.masa_kerja_jabatan < 9 THEN '<9 Tahun'
                             ELSE '>=9 Tahun'
                        END AS category,
                        NULL AS seriesLabel,
                        COUNT(a.id_asn) AS value
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE a.masa_kerja_jabatan IS NOT NULL
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append(" GROUP BY CASE WHEN a.masa_kerja_jabatan < 9 THEN '<9 Tahun' ELSE '>=9 Tahun' END");

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }

    public List<RawChartRow> getMasaKerjaGolongan(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    CASE
                        WHEN a.masa_kerja_golongan < 5 THEN '<5 Tahun'
                        ELSE '>=5 Tahun'
                    END AS category,
                    NULL AS seriesLabel,
                    COUNT(a.id_asn) AS value
                                                FROM asn a
                                                LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                                                LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                                                LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                                                WHERE a.masa_kerja_golongan IS NOT NULL
                                            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        sql.append("""
                                GROUP BY
                CASE
                    WHEN a.masa_kerja_golongan < 5 THEN '<5 Tahun'
                    ELSE '>=5 Tahun'
                END
                            """);

        return jdbc.query(sql.toString(), params,
                (rs, rowNum) -> new RawChartRow(rs.getString("category"), null, rs.getLong("value")));
    }
}

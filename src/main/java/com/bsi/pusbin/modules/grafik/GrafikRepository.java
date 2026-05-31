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
                               Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
                               String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {
        if (instansiId != null) {
            sql.append(" AND a.id_instansi = :instansiId");
            params.addValue("instansiId", instansiId);
        }
        if (jenisAsnId != null) {
            sql.append(" AND a.id_jenis_asn = :jenisAsnId");
            params.addValue("jenisAsnId", jenisAsnId);
        }
        if (nomenklaturId != null) {
            sql.append(" AND j.id_nomenklatur = :nomenklaturId");
            params.addValue("nomenklaturId", nomenklaturId);
        }
        if (jenjang != null) {
            sql.append(" AND j.jenjang = :jenjang");
            params.addValue("jenjang", jenjang);
        }
        if (kategori != null) {
            sql.append(" AND i.kategori = :kategori");
            params.addValue("kategori", kategori);
        }
        if (wilayahPokjaId != null) {
            sql.append(" AND w.id_wilayah_pokja = :wilayahPokjaId");
            params.addValue("wilayahPokjaId", wilayahPokjaId);
        }
        if (namaJabatanId != null) {
            sql.append(" AND a.id_jabatan = :namaJabatanId");
            params.addValue("namaJabatanId", namaJabatanId);
        }
    }

    private void appendFiltersForTrend(StringBuilder sql, MapSqlParameterSource params,
                                      Integer nomenklaturId, String jenjang, Integer namaJabatanId) {
        if (nomenklaturId != null) {
            sql.append(" AND j.id_nomenklatur = :nomenklaturId");
            params.addValue("nomenklaturId", nomenklaturId);
        }
        if (jenjang != null) {
            sql.append(" AND j.jenjang = :jenjang");
            params.addValue("jenjang", jenjang);
        }
        if (namaJabatanId != null) {
            sql.append(" AND t.id_jabatan = :namaJabatanId");
            params.addValue("namaJabatanId", namaJabatanId);
        }
    }

    public List<RawChartRow> getSebaranAsnJenjang(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY j.jenjang, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), rs.getString("seriesLabel"), rs.getLong("value"))
        );
    }

    public List<RawChartRow> getPersentaseGender(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY jk.nama_kelamin");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }

    public List<RawChartRow> getPersentaseAsnJfMasn(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }

    public List<RawChartRow> getSebaranAsnJfmasnInstansi(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(i.jenis_instansi, 'Tanpa Jenis Instansi') AS category,
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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY i.jenis_instansi, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), rs.getString("seriesLabel"), rs.getLong("value"))
        );
    }

    public List<RawChartRow> getSebaranAsnKlpd(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY i.kategori, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), rs.getString("seriesLabel"), rs.getLong("value"))
        );
    }

    public List<RawChartRow> getSebaranAsnJabatan(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY j.nama_jabatan, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), rs.getString("seriesLabel"), rs.getLong("value"))
        );
    }

   public List<RawChartRow> getTrenKenaikanJf(Integer namaJabatanId) {

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

    if (namaJabatanId != null) {
        sql.append(" AND t.id_jabatan = :namaJabatanId");
        params.addValue("namaJabatanId", namaJabatanId);
    }

    sql.append("""
        ORDER BY t.periode ASC
    """);

    return jdbc.query(sql.toString(), params, (rs, rowNum) ->
        new RawChartRow(
            rs.getString("category"),
            rs.getString("seriesLabel"),
            rs.getLong("value")
        )
    );
}
    public List<RawChartRow> getGolonganRuang(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY g.golongan_ruang");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }

    public List<RawChartRow> getPersentaseJfMasn(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY j.nama_jabatan");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }

    public List<RawChartRow> getSebaranKategori(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(jj.nama_jenis_jf, 'Tanpa Kategori') AS category,
                COALESCE(ja.nama_jenis, 'Tanpa Jenis') AS seriesLabel,
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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY jj.nama_jenis_jf, ja.nama_jenis");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), rs.getString("seriesLabel"), rs.getLong("value"))
        );
    }

    public List<RawChartRow> getMasaKerjaJabatan(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY CASE WHEN a.masa_kerja_jabatan < 9 THEN '<9 Tahun' ELSE '>=9 Tahun' END");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }

    public List<RawChartRow> getMasaKerjaGolongan(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                CASE WHEN a.masa_kerja_golongan < 5 THEN '<5 Tahun'
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
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY CASE WHEN a.masa_kerja_golongan < 5 THEN '<5 Tahun' ELSE '>=5 Tahun' END");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new RawChartRow(rs.getString("category"), null, rs.getLong("value"))
        );
    }
}


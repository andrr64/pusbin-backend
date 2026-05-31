package com.bsi.pusbin.modules.table;

import com.bsi.pusbin.modules.table.schema.TableDataRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TableRepository {

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

    public List<TableDataRow> getWilayahKerjaData(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(w.nama_wilker, 'Tanpa Wilayah Kerja') AS label,
                COUNT(a.id_asn) AS count
            FROM asn a
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY w.nama_wilker ORDER BY count DESC, label ASC");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new TableDataRow(rs.getString("label"), rs.getLong("count"))
        );
    }

    public List<TableDataRow> getJabatanData(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(NULLIF(CONCAT_WS(' ', j.nama_jabatan, j.jenjang), ''), 'Tanpa Jabatan') AS label,
                COUNT(a.id_asn) AS count
            FROM asn a
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
            WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY j.nama_jabatan, j.jenjang ORDER BY count DESC, label ASC");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new TableDataRow(rs.getString("label"), rs.getLong("count"))
        );
    }

    public List<TableDataRow> getPendidikanData(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(p.nama_pendidikan, 'Tanpa Pendidikan') AS label,
                COUNT(a.id_asn) AS count
            FROM asn a
            LEFT JOIN pendidikan p ON a.id_pendidikan = p.id_pendidikan
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
            WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY p.nama_pendidikan ORDER BY count DESC, label ASC");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new TableDataRow(rs.getString("label"), rs.getLong("count"))
        );
    }

    public List<TableDataRow> getInstansiData(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId) {

        StringBuilder sql = new StringBuilder("""
            SELECT 
                COALESCE(i.nama_instansi, 'Tanpa Instansi') AS label,
                COUNT(a.id_asn) AS count
            FROM asn a
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
            WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId, namaJabatanId);

        sql.append(" GROUP BY i.nama_instansi ORDER BY count DESC, label ASC");

        return jdbc.query(sql.toString(), params, (rs, rowNum) ->
                new TableDataRow(rs.getString("label"), rs.getLong("count"))
        );
    }
}

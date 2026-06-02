package com.bsi.pusbin.modules.filter;

import com.bsi.pusbin.modules.filter.schema.FilterRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilterRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public List<FilterRow> fetchFilterRows(
            Integer instansiId,
            Integer jenisAsnId,
            Integer nomenklaturId,
            String jenjang,
            String kategori,
            Integer wilayahPokjaId,
            Integer namaJabatanId,
            String jenisInstansi) {

        StringBuilder sql = new StringBuilder("""
                    SELECT DISTINCT
                        asn.id_instansi, instansi.nama_instansi, instansi.kategori,
                        asn.id_jenis_asn, jenis_asn.nama_jenis,
                        jabatan.id_nomenklatur, nomenklatur.nama_nomenklatur,
                        jabatan.jenjang,
                        wilayah_bkn.id_wilayah_pokja, wilayah_pokja.nama_pokja,
                        asn.id_jabatan, jabatan.nama_jabatan, instansi.jenis_instansi
                    FROM asn
                    LEFT JOIN instansi ON asn.id_instansi = instansi.id_instansi
                    LEFT JOIN jenis_asn ON asn.id_jenis_asn = jenis_asn.id_jenis_asn
                    LEFT JOIN jabatan ON asn.id_jabatan = jabatan.id_jabatan
                    LEFT JOIN nomenklatur ON jabatan.id_nomenklatur = nomenklatur.id_nomenklatur
                    LEFT JOIN wilayah_bkn ON instansi.id_wilker = wilayah_bkn.id_wilker
                    LEFT JOIN wilayah_pokja ON wilayah_bkn.id_wilayah_pokja = wilayah_pokja.id_wilayah_pokja
                    WHERE 1=1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (instansiId != null) {
            sql.append(" AND asn.id_instansi = :instansiId");
            params.addValue("instansiId", instansiId);
        }
        if (jenisAsnId != null) {
            sql.append(" AND asn.id_jenis_asn = :jenisAsnId");
            params.addValue("jenisAsnId", jenisAsnId);
        }
        if (nomenklaturId != null) {
            sql.append(" AND jabatan.id_nomenklatur = :nomenklaturId");
            params.addValue("nomenklaturId", nomenklaturId);
        }
        if (jenjang != null) {
            sql.append(" AND jabatan.jenjang = :jenjang");
            params.addValue("jenjang", jenjang);
        }
        if (kategori != null) {
            sql.append(" AND instansi.kategori = :kategori");
            params.addValue("kategori", kategori);
        }
        if (wilayahPokjaId != null) {
            sql.append(" AND wilayah_bkn.id_wilayah_pokja = :wilayahPokjaId");
            params.addValue("wilayahPokjaId", wilayahPokjaId);
        }
        if (namaJabatanId != null) {
            sql.append(" AND asn.id_jabatan = :namaJabatanId");
            params.addValue("namaJabatanId", namaJabatanId);
        }
        if (jenisInstansi != null) {
            sql.append(" AND instansi.jenis_instansi = :jenisInstansi");
            params.addValue("jenisInstansi", jenisInstansi);
        }

        return jdbc.query(sql.toString(), params, (rs, rowNum) -> {
            FilterRow r = new FilterRow();
            r.setIdInstansi(getInteger(rs, "id_instansi"));
            r.setNamaInstansi(rs.getString("nama_instansi"));
            r.setKategori(rs.getString("kategori"));
            r.setIdJenisAsn(getInteger(rs, "id_jenis_asn"));
            r.setNamaJenis(rs.getString("nama_jenis"));
            r.setIdNomenklatur(getInteger(rs, "id_nomenklatur"));
            r.setNamaNomenklatur(rs.getString("nama_nomenklatur"));
            r.setJenjang(rs.getString("jenjang"));
            r.setIdWilayahPokja(getInteger(rs, "id_wilayah_pokja"));
            r.setNamaPokja(rs.getString("nama_pokja"));
            r.setIdJabatan(getInteger(rs, "id_jabatan"));
            r.setNamaJabatan(rs.getString("nama_jabatan"));
            r.setJenisInstansi(rs.getString("jenis_instansi"));
            return r;
        });
    }

    private Integer getInteger(java.sql.ResultSet rs, String col) throws java.sql.SQLException {
        int val = rs.getInt(col);
        return rs.wasNull() ? null : val;
    }
}

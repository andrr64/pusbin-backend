package com.bsi.pusbin.modules.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepository {

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

    public long countTotalPegawai(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(a.id_asn)
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

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    public long countTotalInstansi(
            List<Integer> instansiId, List<Integer> jenisAsnId, List<Integer> nomenklaturId,
            List<String> jenjang, List<String> kategori, List<Integer> wilayahPokjaId, List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId, List<Integer> golonganId, List<Integer> pendidikanId,
            List<String> masaKerjaGolongan, List<String> masaKerjaJabatan,
            List<String> kategoriJf) {

        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(DISTINCT a.id_instansi)
                    FROM asn a
                    LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
                    LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
                    LEFT JOIN wilayah_bkn w ON i.id_wilker = w.id_wilker
                    WHERE 1=1 AND a.id_instansi IS NOT NULL
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendFilters(sql, params, instansiId, jenisAsnId, nomenklaturId, jenjang, kategori, wilayahPokjaId,
                namaJabatanId, jenisInstansi,
                jenisKelaminId, golonganId, pendidikanId, masaKerjaGolongan, masaKerjaJabatan, kategoriJf);

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    public String getLastUpdatedAt() {
        String sql = "SELECT CAST(updated_at AS VARCHAR) FROM asn WHERE updated_at IS NOT NULL ORDER BY updated_at DESC LIMIT 1";
        try {
            return jdbc.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }
}

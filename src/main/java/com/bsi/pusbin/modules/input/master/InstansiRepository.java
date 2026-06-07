package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.InstansiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InstansiRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private InstansiDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstansiDto dto = new InstansiDto();
        int idInstansi = rs.getInt("id_instansi");
        if (!rs.wasNull()) dto.setIdInstansi(idInstansi);
        int idWilker = rs.getInt("id_wilker");
        if (!rs.wasNull()) dto.setIdWilker(idWilker);
        dto.setNamaInstansi(rs.getString("nama_instansi"));
        dto.setKategori(rs.getString("kategori"));
        dto.setJenisInstansi(rs.getString("jenis_instansi"));
        return dto;
    }

    public List<InstansiDto> findAll() {
        return jdbc.query("SELECT * FROM instansi", this::mapRow);
    }

    public Optional<InstansiDto> findById(Integer id) {
        List<InstansiDto> res = jdbc.query("SELECT * FROM instansi WHERE id_instansi = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(InstansiDto dto) {
        String sql = "INSERT INTO instansi (id_wilker, nama_instansi, kategori, jenis_instansi) VALUES (:idWilker, :namaInstansi, :kategori, :jenisInstansi)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idWilker", dto.getIdWilker());
        params.addValue("namaInstansi", dto.getNamaInstansi());
        params.addValue("kategori", dto.getKategori());
        params.addValue("jenisInstansi", dto.getJenisInstansi());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_instansi"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, InstansiDto dto) {
        String sql = "UPDATE instansi SET id_wilker = :idWilker, nama_instansi = :namaInstansi, kategori = :kategori, jenis_instansi = :jenisInstansi WHERE id_instansi = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("idWilker", dto.getIdWilker());
        params.addValue("namaInstansi", dto.getNamaInstansi());
        params.addValue("kategori", dto.getKategori());
        params.addValue("jenisInstansi", dto.getJenisInstansi());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM instansi WHERE id_instansi = :id", new MapSqlParameterSource("id", id));
    }
}

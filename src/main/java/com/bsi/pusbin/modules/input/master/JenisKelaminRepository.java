package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisKelaminDto;
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
public class JenisKelaminRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private JenisKelaminDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        JenisKelaminDto dto = new JenisKelaminDto();
        int idJenisKelamin = rs.getInt("id_jenis_kelamin");
        if (!rs.wasNull()) dto.setIdJenisKelamin(idJenisKelamin);
        dto.setNamaKelamin(rs.getString("nama_kelamin"));
        return dto;
    }

    public List<JenisKelaminDto> findAll() {
        return jdbc.query("SELECT * FROM jenis_kelamin", this::mapRow);
    }

    public Optional<JenisKelaminDto> findById(Integer id) {
        List<JenisKelaminDto> res = jdbc.query("SELECT * FROM jenis_kelamin WHERE id_jenis_kelamin = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(JenisKelaminDto dto) {
        String sql = "INSERT INTO jenis_kelamin (nama_kelamin) VALUES (:namaKelamin)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaKelamin", dto.getNamaKelamin());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_jenis_kelamin"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, JenisKelaminDto dto) {
        String sql = "UPDATE jenis_kelamin SET nama_kelamin = :namaKelamin WHERE id_jenis_kelamin = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaKelamin", dto.getNamaKelamin());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM jenis_kelamin WHERE id_jenis_kelamin = :id", new MapSqlParameterSource("id", id));
    }
}

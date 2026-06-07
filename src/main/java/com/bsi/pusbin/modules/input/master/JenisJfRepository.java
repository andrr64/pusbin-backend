package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisJfDto;
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
public class JenisJfRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private JenisJfDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        JenisJfDto dto = new JenisJfDto();
        int idJenisJf = rs.getInt("id_jenis_jf");
        if (!rs.wasNull()) dto.setIdJenisJf(idJenisJf);
        dto.setNamaJenisJf(rs.getString("nama_jenis_jf"));
        return dto;
    }

    public List<JenisJfDto> findAll() {
        return jdbc.query("SELECT * FROM jenis_jf", this::mapRow);
    }

    public Optional<JenisJfDto> findById(Integer id) {
        List<JenisJfDto> res = jdbc.query("SELECT * FROM jenis_jf WHERE id_jenis_jf = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(JenisJfDto dto) {
        String sql = "INSERT INTO jenis_jf (nama_jenis_jf) VALUES (:namaJenisJf)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaJenisJf", dto.getNamaJenisJf());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_jenis_jf"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, JenisJfDto dto) {
        String sql = "UPDATE jenis_jf SET nama_jenis_jf = :namaJenisJf WHERE id_jenis_jf = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaJenisJf", dto.getNamaJenisJf());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM jenis_jf WHERE id_jenis_jf = :id", new MapSqlParameterSource("id", id));
    }
}

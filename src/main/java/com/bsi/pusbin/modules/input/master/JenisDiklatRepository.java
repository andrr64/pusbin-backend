package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisDiklatDto;
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
public class JenisDiklatRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private JenisDiklatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        JenisDiklatDto dto = new JenisDiklatDto();
        int idJenisDiklat = rs.getInt("id_jenis_diklat");
        if (!rs.wasNull()) dto.setIdJenisDiklat(idJenisDiklat);
        dto.setNamaJenisDiklat(rs.getString("nama_jenis_diklat"));
        return dto;
    }

    public List<JenisDiklatDto> findAll() {
        return jdbc.query("SELECT * FROM jenis_diklat", this::mapRow);
    }

    public Optional<JenisDiklatDto> findById(Integer id) {
        List<JenisDiklatDto> res = jdbc.query("SELECT * FROM jenis_diklat WHERE id_jenis_diklat = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(JenisDiklatDto dto) {
        String sql = "INSERT INTO jenis_diklat (nama_jenis_diklat) VALUES (:namaJenisDiklat)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaJenisDiklat", dto.getNamaJenisDiklat());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_jenis_diklat"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, JenisDiklatDto dto) {
        String sql = "UPDATE jenis_diklat SET nama_jenis_diklat = :namaJenisDiklat WHERE id_jenis_diklat = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaJenisDiklat", dto.getNamaJenisDiklat());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM jenis_diklat WHERE id_jenis_diklat = :id", new MapSqlParameterSource("id", id));
    }
}

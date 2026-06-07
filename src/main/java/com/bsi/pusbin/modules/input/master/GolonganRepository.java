package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.GolonganDto;
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
public class GolonganRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private GolonganDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        GolonganDto dto = new GolonganDto();
        int idGolongan = rs.getInt("id_golongan");
        if (!rs.wasNull()) dto.setIdGolongan(idGolongan);
        dto.setGolonganRuang(rs.getString("golongan_ruang"));
        return dto;
    }

    public List<GolonganDto> findAll() {
        return jdbc.query("SELECT * FROM golongan", this::mapRow);
    }

    public Optional<GolonganDto> findById(Integer id) {
        List<GolonganDto> res = jdbc.query("SELECT * FROM golongan WHERE id_golongan = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(GolonganDto dto) {
        String sql = "INSERT INTO golongan (golongan_ruang) VALUES (:golonganRuang)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("golonganRuang", dto.getGolonganRuang());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_golongan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, GolonganDto dto) {
        String sql = "UPDATE golongan SET golongan_ruang = :golonganRuang WHERE id_golongan = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("golonganRuang", dto.getGolonganRuang());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM golongan WHERE id_golongan = :id", new MapSqlParameterSource("id", id));
    }
}

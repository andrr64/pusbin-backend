package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.NomenklaturDto;
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
public class NomenklaturRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private NomenklaturDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        NomenklaturDto dto = new NomenklaturDto();
        int idNomenklatur = rs.getInt("id_nomenklatur");
        if (!rs.wasNull()) dto.setIdNomenklatur(idNomenklatur);
        dto.setNamaNomenklatur(rs.getString("nama_nomenklatur"));
        return dto;
    }

    public List<NomenklaturDto> findAll() {
        return jdbc.query("SELECT * FROM nomenklatur", this::mapRow);
    }

    public Optional<NomenklaturDto> findById(Integer id) {
        List<NomenklaturDto> res = jdbc.query("SELECT * FROM nomenklatur WHERE id_nomenklatur = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(NomenklaturDto dto) {
        String sql = "INSERT INTO nomenklatur (nama_nomenklatur) VALUES (:namaNomenklatur)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaNomenklatur", dto.getNamaNomenklatur());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_nomenklatur"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, NomenklaturDto dto) {
        String sql = "UPDATE nomenklatur SET nama_nomenklatur = :namaNomenklatur WHERE id_nomenklatur = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaNomenklatur", dto.getNamaNomenklatur());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM nomenklatur WHERE id_nomenklatur = :id", new MapSqlParameterSource("id", id));
    }
}

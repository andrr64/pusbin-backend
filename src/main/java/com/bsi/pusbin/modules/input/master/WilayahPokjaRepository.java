package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.WilayahPokjaDto;
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
public class WilayahPokjaRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private WilayahPokjaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        WilayahPokjaDto dto = new WilayahPokjaDto();
        int idWilayahPokja = rs.getInt("id_wilayah_pokja");
        if (!rs.wasNull()) dto.setIdWilayahPokja(idWilayahPokja);
        dto.setNamaPokja(rs.getString("nama_pokja"));
        return dto;
    }

    public List<WilayahPokjaDto> findAll() {
        return jdbc.query("SELECT * FROM wilayah_pokja", this::mapRow);
    }

    public Optional<WilayahPokjaDto> findById(Integer id) {
        List<WilayahPokjaDto> res = jdbc.query("SELECT * FROM wilayah_pokja WHERE id_wilayah_pokja = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(WilayahPokjaDto dto) {
        String sql = "INSERT INTO wilayah_pokja (nama_pokja) VALUES (:namaPokja)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaPokja", dto.getNamaPokja());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_wilayah_pokja"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, WilayahPokjaDto dto) {
        String sql = "UPDATE wilayah_pokja SET nama_pokja = :namaPokja WHERE id_wilayah_pokja = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaPokja", dto.getNamaPokja());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM wilayah_pokja WHERE id_wilayah_pokja = :id", new MapSqlParameterSource("id", id));
    }
}

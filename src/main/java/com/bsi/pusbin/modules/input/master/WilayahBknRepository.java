package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.WilayahBknDto;
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
public class WilayahBknRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private WilayahBknDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        WilayahBknDto dto = new WilayahBknDto();
        int idWilker = rs.getInt("id_wilker");
        if (!rs.wasNull()) dto.setIdWilker(idWilker);
        dto.setNamaWilker(rs.getString("nama_wilker"));
        int noUrut = rs.getInt("no_urut");
        if (!rs.wasNull()) dto.setNoUrut(noUrut);
        int idWilayahPokja = rs.getInt("id_wilayah_pokja");
        if (!rs.wasNull()) dto.setIdWilayahPokja(idWilayahPokja);
        return dto;
    }

    public List<WilayahBknDto> findAll() {
        return jdbc.query("SELECT * FROM wilayah_bkn", this::mapRow);
    }

    public Optional<WilayahBknDto> findById(Integer id) {
        List<WilayahBknDto> res = jdbc.query("SELECT * FROM wilayah_bkn WHERE id_wilker = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(WilayahBknDto dto) {
        String sql = "INSERT INTO wilayah_bkn (nama_wilker, no_urut, id_wilayah_pokja) VALUES (:namaWilker, :noUrut, :idWilayahPokja)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaWilker", dto.getNamaWilker());
        params.addValue("noUrut", dto.getNoUrut());
        params.addValue("idWilayahPokja", dto.getIdWilayahPokja());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_wilker"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, WilayahBknDto dto) {
        String sql = "UPDATE wilayah_bkn SET nama_wilker = :namaWilker, no_urut = :noUrut, id_wilayah_pokja = :idWilayahPokja WHERE id_wilker = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaWilker", dto.getNamaWilker());
        params.addValue("noUrut", dto.getNoUrut());
        params.addValue("idWilayahPokja", dto.getIdWilayahPokja());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM wilayah_bkn WHERE id_wilker = :id", new MapSqlParameterSource("id", id));
    }
}

package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.KedudukanAsnDto;
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
public class KedudukanAsnRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private KedudukanAsnDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        KedudukanAsnDto dto = new KedudukanAsnDto();
        int idKedudukan = rs.getInt("id_kedudukan");
        if (!rs.wasNull()) dto.setIdKedudukan(idKedudukan);
        dto.setNamaKedudukan(rs.getString("nama_kedudukan"));
        return dto;
    }

    public List<KedudukanAsnDto> findAll() {
        return jdbc.query("SELECT * FROM kedudukan_asn", this::mapRow);
    }

    public Optional<KedudukanAsnDto> findById(Integer id) {
        List<KedudukanAsnDto> res = jdbc.query("SELECT * FROM kedudukan_asn WHERE id_kedudukan = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(KedudukanAsnDto dto) {
        String sql = "INSERT INTO kedudukan_asn (nama_kedudukan) VALUES (:namaKedudukan)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaKedudukan", dto.getNamaKedudukan());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_kedudukan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, KedudukanAsnDto dto) {
        String sql = "UPDATE kedudukan_asn SET nama_kedudukan = :namaKedudukan WHERE id_kedudukan = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaKedudukan", dto.getNamaKedudukan());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM kedudukan_asn WHERE id_kedudukan = :id", new MapSqlParameterSource("id", id));
    }
}

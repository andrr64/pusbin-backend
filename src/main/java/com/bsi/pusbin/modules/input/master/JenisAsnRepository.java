package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisAsnDto;
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
public class JenisAsnRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private JenisAsnDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        JenisAsnDto dto = new JenisAsnDto();
        int idJenisAsn = rs.getInt("id_jenis_asn");
        if (!rs.wasNull()) dto.setIdJenisAsn(idJenisAsn);
        dto.setNamaJenis(rs.getString("nama_jenis"));
        return dto;
    }

    public List<JenisAsnDto> findAll() {
        return jdbc.query("SELECT * FROM jenis_asn", this::mapRow);
    }

    public Optional<JenisAsnDto> findById(Integer id) {
        List<JenisAsnDto> res = jdbc.query("SELECT * FROM jenis_asn WHERE id_jenis_asn = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(JenisAsnDto dto) {
        String sql = "INSERT INTO jenis_asn (nama_jenis) VALUES (:namaJenis)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("namaJenis", dto.getNamaJenis());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_jenis_asn"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, JenisAsnDto dto) {
        String sql = "UPDATE jenis_asn SET nama_jenis = :namaJenis WHERE id_jenis_asn = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("namaJenis", dto.getNamaJenis());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM jenis_asn WHERE id_jenis_asn = :id", new MapSqlParameterSource("id", id));
    }
}

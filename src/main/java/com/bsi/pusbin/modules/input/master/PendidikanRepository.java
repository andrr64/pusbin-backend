package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.PendidikanDto;
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
public class PendidikanRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private PendidikanDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PendidikanDto dto = new PendidikanDto();
        int idPendidikan = rs.getInt("id_pendidikan");
        if (!rs.wasNull()) dto.setIdPendidikan(idPendidikan);
        dto.setTingkat(rs.getString("tingkat"));
        dto.setNamaPendidikan(rs.getString("nama_pendidikan"));
        return dto;
    }

    public List<PendidikanDto> findAll() {
        return jdbc.query("SELECT * FROM pendidikan", this::mapRow);
    }

    public Optional<PendidikanDto> findById(Integer id) {
        List<PendidikanDto> res = jdbc.query("SELECT * FROM pendidikan WHERE id_pendidikan = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(PendidikanDto dto) {
        String sql = "INSERT INTO pendidikan (tingkat, nama_pendidikan) VALUES (:tingkat, :namaPendidikan)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tingkat", dto.getTingkat());
        params.addValue("namaPendidikan", dto.getNamaPendidikan());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_pendidikan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, PendidikanDto dto) {
        String sql = "UPDATE pendidikan SET tingkat = :tingkat, nama_pendidikan = :namaPendidikan WHERE id_pendidikan = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("tingkat", dto.getTingkat());
        params.addValue("namaPendidikan", dto.getNamaPendidikan());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM pendidikan WHERE id_pendidikan = :id", new MapSqlParameterSource("id", id));
    }
}

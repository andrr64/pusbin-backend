package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.TotalAsnPeriodeDto;
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
public class TotalAsnPeriodeRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private TotalAsnPeriodeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        TotalAsnPeriodeDto dto = new TotalAsnPeriodeDto();
        long id = rs.getLong("id");
        if (!rs.wasNull()) dto.setId(id);
        int jumlahAsn = rs.getInt("jumlah_asn");
        if (!rs.wasNull()) dto.setJumlahAsn(jumlahAsn);
        java.sql.Date periodeDate = rs.getDate("periode");
        if (periodeDate != null) dto.setPeriode(periodeDate.toLocalDate());
        int idJabatan = rs.getInt("id_jabatan");
        if (!rs.wasNull()) dto.setIdJabatan(idJabatan);
        return dto;
    }

    public List<TotalAsnPeriodeDto> findAll() {
        return jdbc.query("SELECT * FROM total_asn_periode_by_nama_jabatan", this::mapRow);
    }

    public Optional<TotalAsnPeriodeDto> findById(Long id) {
        List<TotalAsnPeriodeDto> res = jdbc.query("SELECT * FROM total_asn_periode_by_nama_jabatan WHERE id = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Long insert(TotalAsnPeriodeDto dto) {
        String sql = "INSERT INTO total_asn_periode_by_nama_jabatan (jumlah_asn, periode, id_jabatan) VALUES (:jumlahAsn, :periode, :idJabatan)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jumlahAsn", dto.getJumlahAsn());
        params.addValue("periode", dto.getPeriode());
        params.addValue("idJabatan", dto.getIdJabatan());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public void update(Long id, TotalAsnPeriodeDto dto) {
        String sql = "UPDATE total_asn_periode_by_nama_jabatan SET jumlah_asn = :jumlahAsn, periode = :periode, id_jabatan = :idJabatan WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("jumlahAsn", dto.getJumlahAsn());
        params.addValue("periode", dto.getPeriode());
        params.addValue("idJabatan", dto.getIdJabatan());
        jdbc.update(sql, params);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM total_asn_periode_by_nama_jabatan WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}

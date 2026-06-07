package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JabatanDto;
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
public class JabatanRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private JabatanDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        JabatanDto dto = new JabatanDto();
        int idJabatan = rs.getInt("id_jabatan");
        if (!rs.wasNull()) dto.setIdJabatan(idJabatan);
        int idNomenklatur = rs.getInt("id_nomenklatur");
        if (!rs.wasNull()) dto.setIdNomenklatur(idNomenklatur);
        int idJenisJf = rs.getInt("id_jenis_jf");
        if (!rs.wasNull()) dto.setIdJenisJf(idJenisJf);
        dto.setNamaJabatan(rs.getString("nama_jabatan"));
        dto.setJenjang(rs.getString("jenjang"));
        return dto;
    }

    public List<JabatanDto> findAll() {
        return jdbc.query("SELECT * FROM jabatan", this::mapRow);
    }

    public Optional<JabatanDto> findById(Integer id) {
        List<JabatanDto> res = jdbc.query("SELECT * FROM jabatan WHERE id_jabatan = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(JabatanDto dto) {
        String sql = "INSERT INTO jabatan (id_nomenklatur, id_jenis_jf, nama_jabatan, jenjang) VALUES (:idNomenklatur, :idJenisJf, :namaJabatan, :jenjang)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idNomenklatur", dto.getIdNomenklatur());
        params.addValue("idJenisJf", dto.getIdJenisJf());
        params.addValue("namaJabatan", dto.getNamaJabatan());
        params.addValue("jenjang", dto.getJenjang());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id_jabatan"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, JabatanDto dto) {
        String sql = "UPDATE jabatan SET id_nomenklatur = :idNomenklatur, id_jenis_jf = :idJenisJf, nama_jabatan = :namaJabatan, jenjang = :jenjang WHERE id_jabatan = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("idNomenklatur", dto.getIdNomenklatur());
        params.addValue("idJenisJf", dto.getIdJenisJf());
        params.addValue("namaJabatan", dto.getNamaJabatan());
        params.addValue("jenjang", dto.getJenjang());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM jabatan WHERE id_jabatan = :id", new MapSqlParameterSource("id", id));
    }
}

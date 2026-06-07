package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.UsersDto;
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
public class UsersRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private UsersDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsersDto dto = new UsersDto();
        int id = rs.getInt("id");
        if (!rs.wasNull()) dto.setId(id);
        dto.setNip(rs.getString("nip"));
        dto.setPasswordHash(rs.getString("password_hash"));
        return dto;
    }

    public List<UsersDto> findAll() {
        return jdbc.query("SELECT * FROM users", this::mapRow);
    }

    public Optional<UsersDto> findById(Integer id) {
        List<UsersDto> res = jdbc.query("SELECT * FROM users WHERE id = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }

    public Integer insert(UsersDto dto) {
        String sql = "INSERT INTO users (nip, password_hash) VALUES (:nip, :passwordHash)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nip", dto.getNip());
        params.addValue("passwordHash", dto.getPasswordHash());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    public void update(Integer id, UsersDto dto) {
        String sql = "UPDATE users SET nip = :nip, password_hash = :passwordHash WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        params.addValue("nip", dto.getNip());
        params.addValue("passwordHash", dto.getPasswordHash());
        jdbc.update(sql, params);
    }

    public void delete(Integer id) {
        jdbc.update("DELETE FROM users WHERE id = :id", new MapSqlParameterSource("id", id));
    }
}

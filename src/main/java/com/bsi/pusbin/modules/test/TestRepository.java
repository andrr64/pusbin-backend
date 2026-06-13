package com.bsi.pusbin.modules.test;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestRepository {

    private final JdbcTemplate jdbc;

    public String getWelcomeMessage() {
        return jdbc.queryForObject("SELECT 'Halo hana dan ambar, server berhasil dijalankan!'", String.class);
    }

    public String getTestServerMessage() {
        return jdbc.queryForObject("SELECT 'Server is running'", String.class);
    }
}

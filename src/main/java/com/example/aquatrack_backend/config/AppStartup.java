package com.example.aquatrack_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class AppStartup implements ApplicationRunner {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AppStartup(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String sql = "UPDATE hibernate_sequences SET next_val = (SELECT MAX(id) FROM (SELECT id FROM cliente UNION SELECT id FROM empleado) AS ids) WHERE sequence_name = 'default'";
        jdbcTemplate.update(sql);
    }
}

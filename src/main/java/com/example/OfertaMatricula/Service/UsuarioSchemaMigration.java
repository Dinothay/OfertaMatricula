package com.example.OfertaMatricula.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UsuarioSchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void migrateUsuarioSchema() {
        Integer usernameExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'usuario' AND column_name = 'username'",
                Integer.class
        );

        Integer loginExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'usuario' AND column_name = 'login'",
                Integer.class
        );

        if (usernameExists != null && usernameExists == 0 && loginExists != null && loginExists > 0) {
            jdbcTemplate.execute("ALTER TABLE usuario CHANGE COLUMN login username VARCHAR(255) NOT NULL");
        }
    }
}

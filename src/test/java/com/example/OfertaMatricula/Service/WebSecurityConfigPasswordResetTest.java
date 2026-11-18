package com.example.OfertaMatricula.Service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;

class WebSecurityConfigPasswordResetTest {

    private final WebSecurityConfig config = new WebSecurityConfig();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void deveExigirTrocaDeSenhaQuandoSenhaFoiCriadaVazia() {
        Usuario usuario = new Usuario("aluno", "", Papel.ALUNO, null);

        assertTrue(config.isPasswordResetRequired(usuario, passwordEncoder));
    }

    @Test
    void naoDeveExigirTrocaDeSenhaQuandoSenhaJaFoiDefinida() {
        Usuario usuario = new Usuario("aluno", passwordEncoder.encode("123456"), Papel.ALUNO, null);

        assertFalse(config.isPasswordResetRequired(usuario, passwordEncoder));
    }
}

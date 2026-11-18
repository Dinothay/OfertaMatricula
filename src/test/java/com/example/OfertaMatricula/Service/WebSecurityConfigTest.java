package com.example.OfertaMatricula.Service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class WebSecurityConfigTest {

    @Test
    void deveRedirecionarAlunoParaIndexAlunoAoLogar() throws Exception {
        WebSecurityConfig config = new WebSecurityConfig();
        AuthenticationSuccessHandler handler = config.passwordResetSuccessHandler(config.passwordEncoder());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setUsername("aluno01");
        usuario.setPassword("senha123");
        usuario.setPapel(Papel.ALUNO);

        when(request.getContextPath()).thenReturn("/ofertaMatricula");
        when(authentication.getPrincipal()).thenReturn(usuario);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/ofertaMatricula/indexAluno");
    }
}

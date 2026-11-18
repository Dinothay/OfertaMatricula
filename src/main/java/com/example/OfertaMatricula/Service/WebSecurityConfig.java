package com.example.OfertaMatricula.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationSuccessHandler passwordResetSuccessHandler) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/images/**", "/js/**", "/webjars/**").permitAll()

                        .requestMatchers("/listaLivro", "/listaLivrosAluno", "/listaProva",
                                "/meuPerfil", "/editarUsuario/**", "/atualizarUsuario")
                        .hasAnyRole("ALUNO", "PROFESSOR", "BIBLIOTECARIO", "ADMIN")

                        .requestMatchers("/indexBibliotecario",
                                "/livro", "/cadastraLivro", "/listaLivro", "/listaLivrosAluno",
                                "/editarLivro/**", "/atualizarLivro", "/excluirLivro/**", "/devolverLivro/**",
                                "/emprestimo", "/cadastraEmprestimo", "/listaEmprestimo",
                                "/editarEmprestimo/**", "/atualizarEmprestimo",
                                "/excluirEmprestimo/**", "/removerEmprestimo/**",
                                "/reserva", "/cadastraReserva", "/listaReserva",
                                "/editarReserva/**", "/atualizarReserva", "/excluirReserva/**",
                                "/meuPerfil", "/editarUsuario/**", "/atualizarUsuario",
                                "/editarBibliotecario/**", "/atualizarBibliotecario")
                        .hasRole("BIBLIOTECARIO")

                        .requestMatchers("/indexAluno", "/meuPainelAluno", "/listaLivro",
                                "/pegarLivroEmprestimo/**", "/reservarLivro/**",
                                "/listaOfertaDisciplinas", "/matricularEmOferta/**",
                                "/listaProva", "/meuPerfil", "/editarUsuario/**",
                                "/atualizarUsuario", "/editarAluno/**", "/atualizarAluno",
                                "/renovarMeuEmprestimo/**", "/excluirMinhaReserva/**")
                        .hasRole("ALUNO")

                        .requestMatchers("/indexProfessor", "/meuPainelProfessor", "/listaLivro",
                                "/pegarLivroEmprestimo/**", "/reservarLivro/**",
                                "/listaOfertaDisciplinas", "/listaProva", "/editarProva/**",
                                "/meuPerfil", "/editarUsuario/**", "/atualizarUsuario",
                                "/editarProfessor/**", "/atualizarProfessor",
                                "/renovarMeuEmprestimo/**", "/excluirMinhaReserva/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers("/**").hasRole("ADMIN")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                                var authentication = SecurityContextHolder.getContext().getAuthentication();
                                if (authentication != null && authentication.isAuthenticated()) {
                                        boolean isAluno = authentication.getAuthorities().stream()
                                                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ALUNO"));
                                        boolean isProfessor = authentication.getAuthorities().stream()
                                                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_PROFESSOR"));
                                        boolean isBibliotecario = authentication.getAuthorities().stream()
                                                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_BIBLIOTECARIO"));
                                        boolean isAdmin = authentication.getAuthorities().stream()
                                                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

                                        if (isAluno) {
                                                response.sendRedirect(request.getContextPath() + "/indexAluno");
                                                return;
                                        }
                                        if (isProfessor) {
                                                response.sendRedirect(request.getContextPath() + "/indexProfessor");
                                                return;
                                        }
                                        if (isBibliotecario) {
                                                response.sendRedirect(request.getContextPath() + "/indexBibliotecario");
                                                return;
                                        }
                                        if (isAdmin) {
                                                response.sendRedirect(request.getContextPath() + "/");
                                                return;
                                        }
                                }
                                response.sendRedirect(request.getContextPath() + "/login");
                        })
                )
                .formLogin(form -> form.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(passwordResetSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler passwordResetSuccessHandler(PasswordEncoder passwordEncoder) {
        return (request, response, authentication) -> {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            if (isPasswordResetRequired(usuario, passwordEncoder)) {
                response.sendRedirect(request.getContextPath() + "/editarUsuario/" + usuario.getId() + "?firstLogin=true");
                return;
            }
            if (usuario.getPapel() == Papel.ALUNO) {
                response.sendRedirect(request.getContextPath() + "/indexAluno");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return;
            }
            if (usuario.getPapel() == Papel.PROFESSOR) {
                response.sendRedirect(request.getContextPath() + "/indexProfessor");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return;
            }
            if (usuario.getPapel() == Papel.BIBLIOTECARIO) {
                response.sendRedirect(request.getContextPath() + "/indexBibliotecario");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        };
    }

    public boolean isPasswordResetRequired(Usuario usuario, PasswordEncoder passwordEncoder) {
        if (usuario == null) {
            return false;
        }

        String rawPassword = usuario.getPassword();
        return rawPassword == null || rawPassword.isBlank();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {
                if (rawPassword == null || rawPassword.toString().isBlank()) {
                    return "";
                }
                return delegate.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || rawPassword.toString().isBlank()) {
                    return encodedPassword == null || encodedPassword.isBlank();
                }
                return delegate.matches(rawPassword, encodedPassword);
            }
        };
    }
}


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
    public SecurityFilterChain configure(
            HttpSecurity http,
            AuthenticationSuccessHandler passwordResetSuccessHandler) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorize -> authorize

                        // Acesso público
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/webjars/**"
                        ).permitAll()

                        // Funcionalidades compartilhadas
                        .requestMatchers(
                                "/listaLivro",
                                "/listaLivrosAluno",
                                "/listaProva",
                                "/meuPerfil",
                                "/editarUsuario/**",
                                "/atualizarUsuario"
                        ).hasAnyRole(
                                "ALUNO",
                                "PROFESSOR",
                                "BIBLIOTECARIO",
                                "ADMIN"
                        )

                        // Empréstimos e reservas
                        .requestMatchers(
                                "/pegarLivroEmprestimo/**",
                                "/reservarLivro/**",
                                "/renovarMeuEmprestimo/**",
                                "/excluirMinhaReserva/**"
                        ).hasAnyRole(
                                "ALUNO",
                                "PROFESSOR",
                                "ADMIN"
                        )

                        // Funcionalidades do bibliotecário
                        .requestMatchers(
                                "/indexBibliotecario",
                                "/livro",
                                "/cadastraLivro",
                                "/editarLivro/**",
                                "/atualizarLivro",
                                "/excluirLivro/**",
                                "/devolverLivro/**",
                                "/emprestimo",
                                "/cadastraEmprestimo",
                                "/listaEmprestimo",
                                "/editarEmprestimo/**",
                                "/atualizarEmprestimo",
                                "/excluirEmprestimo/**",
                                "/removerEmprestimo/**",
                                "/reserva",
                                "/cadastraReserva",
                                "/listaReserva",
                                "/editarReserva/**",
                                "/atualizarReserva",
                                "/excluirReserva/**",
                                "/editarBibliotecario/**",
                                "/atualizarBibliotecario"
                        ).hasAnyRole("BIBLIOTECARIO", "ADMIN")

                        // Funcionalidades do aluno
                        .requestMatchers(
                                "/indexAluno",
                                "/meuPainelAluno",
                                "/listaOfertaDisciplinas",
                                "/matricularEmOferta/**",
                                "/editarAluno/**",
                                "/atualizarAluno"
                        ).hasAnyRole("ALUNO", "ADMIN")

                        // Funcionalidades do professor
                        .requestMatchers(
                                "/indexProfessor",
                                "/meuPainelProfessor",
                                "/listaOfertaDisciplinas",
                                "/cadastraProva",
                                "/cadastrarProva",
                                "/editarProva/**",
                                "/atualizarProva",
                                "/excluirProva/**",
                                "/editarProfessor/**",
                                "/atualizarProfessor",
                                "/prova"
                        ).hasAnyRole("PROFESSOR", "ADMIN")

                        // Qualquer outra rota: somente administrador
                        .requestMatchers("/**").hasRole("ADMIN")
                )

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(
                                        request.getContextPath() + "/login"
                                )
                        )

                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            var authentication =
                                    SecurityContextHolder.getContext().getAuthentication();

                            if (authentication != null && authentication.isAuthenticated()) {

                                boolean isAluno = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

                                boolean isProfessor = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"));

                                boolean isBibliotecario = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_BIBLIOTECARIO"));

                                boolean isAdmin = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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

                .formLogin(form -> form
                        .loginPage("/login")
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
    public AuthenticationSuccessHandler passwordResetSuccessHandler(
            PasswordEncoder passwordEncoder) {

        return (request, response, authentication) -> {

            Usuario usuario = (Usuario) authentication.getPrincipal();

            if (isPasswordResetRequired(usuario, passwordEncoder)) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/editarUsuario/"
                                + usuario.getId()
                                + "?firstLogin=true"
                );
                return;
            }

            if (usuario.getPapel() == Papel.ALUNO) {
                response.sendRedirect(request.getContextPath() + "/indexAluno");

            } else if (usuario.getPapel() == Papel.PROFESSOR) {
                response.sendRedirect(request.getContextPath() + "/indexProfessor");

            } else if (usuario.getPapel() == Papel.BIBLIOTECARIO) {
                response.sendRedirect(request.getContextPath() + "/indexBibliotecario");

            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        };
    }

    public boolean isPasswordResetRequired(
            Usuario usuario,
            PasswordEncoder passwordEncoder) {

        if (usuario == null) {
            return false;
        }

        String rawPassword = usuario.getPassword();

        return rawPassword == null || rawPassword.isBlank();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new PasswordEncoder() {

            private final BCryptPasswordEncoder delegate =
                    new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {

                if (rawPassword == null || rawPassword.toString().isBlank()) {
                    return "";
                }

                return delegate.encode(rawPassword);
            }

            @Override
            public boolean matches(
                    CharSequence rawPassword,
                    String encodedPassword) {

                if (rawPassword == null || rawPassword.toString().isBlank()) {
                    return encodedPassword == null || encodedPassword.isBlank();
                }

                return delegate.matches(rawPassword, encodedPassword);
            }
        };
    }
}

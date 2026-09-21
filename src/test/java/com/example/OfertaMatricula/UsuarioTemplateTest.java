package com.example.OfertaMatricula;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class UsuarioTemplateTest {

    @Test
    void deveExistirTemplateDaListaDeUsuarios() throws Exception {
        assertTrue(new ClassPathResource("templates/listaUsuarios.html").exists(),
                "O template da lista de usuários não foi encontrado no caminho esperado");
    }
}

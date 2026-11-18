package com.example.OfertaMatricula.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class LivroServiceTest {

    @Test
    void deveSalvarImagemDoLivroNoDiretorioDoProjeto() throws Exception {
        LivroService livroService = new LivroService();

        MockMultipartFile arquivo = new MockMultipartFile(
                "capalmagem",
                "capa-livro.png",
                "image/png",
                "conteudo-da-imagem".getBytes()
        );

        String caminho = livroService.salvarLogo(arquivo);

        assertNotNull(caminho);
        assertTrue(caminho.startsWith("images/"));
        assertTrue(Files.exists(Path.of("src/main/resources/static/images").resolve(Path.of(caminho).getFileName())));
    }
}

package com.example.OfertaMatricula.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {
    private static final Path ENDERECO_ARMAZENAMENTO_ARQUIVO = Paths.get("src/main/resources/static/images").toAbsolutePath().normalize();

    public String salvarFoto(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo da imagem está vazio");
        }

        Files.createDirectories(ENDERECO_ARMAZENAMENTO_ARQUIVO);

        String nomeOriginal = StringUtils.cleanPath(arquivo.getOriginalFilename());
        String nomeSeguro = UUID.randomUUID() + "_" + nomeOriginal;
        Path caminhoArquivo = ENDERECO_ARMAZENAMENTO_ARQUIVO.resolve(nomeSeguro).normalize();

        if (!caminhoArquivo.startsWith(ENDERECO_ARMAZENAMENTO_ARQUIVO)) {
            throw new SecurityException("Nome do arquivo não é viável!");
        }

        Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
        return "images/" + nomeSeguro;
    }
}
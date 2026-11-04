package com.example.OfertaMatricula.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.OfertaMatricula.Repository.UsuarioRepository;

@ControllerAdvice
public class UsuarioLogadoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @ModelAttribute
    public void addUsuarioLogado(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
    }
}
package com.example.OfertaMatricula.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping({"/", "/index"})
    public String index(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        return "index.html";
    }

    @GetMapping("/biblioteca")
    public String biblioteca() {
        return "biblioteca.html";
    }

    @GetMapping("/meuPerfil")
    public String meuPerfil(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        return "meuPerfil.html";
    }
}
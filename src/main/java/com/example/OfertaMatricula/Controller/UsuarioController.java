package com.example.OfertaMatricula.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.UsuarioRepository;
import com.example.OfertaMatricula.Service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuario")
    public String cadastroUsuarios(Model model) {
        model.addAttribute("papeis", Papel.values());
        return "formularioUsuario.html";
    }

    @PostMapping("/cadastrarUsuarios")
    public String saveUsuarios(
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam Papel papel,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            RedirectAttributes ra) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            ra.addFlashAttribute("mensagem","Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("username", username);
            ra.addFlashAttribute("papel", papel);
            return "redirect:/usuario";
        }
        String caminhoFoto = null;
        try {
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                caminhoFoto = usuarioService.salvarFoto(fotoPerfil);
            }
        } catch (IOException e) {
            ra.addFlashAttribute("mensagem", "Erro ao salvar a foto!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/usuario";
        }
        String passwordCriptografada = password == null || password.isBlank() ? "" : passwordEncoder.encode(password);
        Usuario usuario = new Usuario(username, passwordCriptografada, papel, caminhoFoto);
        usuarioRepository.save(usuario);
        ra.addFlashAttribute("mensagem","Usuário cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaUsuarios";
    }

    @GetMapping("/listaUsuarios")
    public String listaUsuarios(Model model) {
        List<Usuario> listaUsuarios = usuarioRepository.findAll();
        model.addAttribute("listaUsuarios", listaUsuarios);
        return "listaUsuarios.html";
    }

    @GetMapping("/editarUsuario/{id}")
    public String editarUsuario(
            @PathVariable Long id,
            @RequestParam(value = "firstLogin", required = false, defaultValue = "false") boolean firstLogin,
            Model model,
            Principal principal,
            RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            ra.addFlashAttribute("mensagem", "Usuário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaUsuarios";
        }

        Usuario usuarioLogado = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuarioLogado != null && !usuarioLogado.getId().equals(id)
                && usuarioLogado.getPapel() != Papel.ADMIN) {
            ra.addFlashAttribute("mensagem", "Você só pode editar sua própria conta.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/meuPerfil";
        }

        if (firstLogin) {
            model.addAttribute("mensagem", "Primeiro acesso detectado: defina uma nova senha para continuar.");
            model.addAttribute("mensagemTipo", "erro");
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLogado", usuarioLogado);
        model.addAttribute("podeEditarPapel", usuarioLogado != null && usuarioLogado.getPapel() == Papel.ADMIN);
        model.addAttribute("papeis", Papel.values());
        return "editarUsuario.html";
    }

    @PostMapping("/atualizarUsuario")
    public String atualizarUsuario(@RequestParam Long id,
            @RequestParam String username,
            @RequestParam(required = false, defaultValue = "") String password,
            @RequestParam(required = false) Papel papel,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            RedirectAttributes ra,
            Principal principal) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            ra.addFlashAttribute("mensagem", "Usuário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaUsuarios";
        }

        Usuario usuarioLogado = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;
        if (usuarioLogado != null && !usuarioLogado.getId().equals(id)
                && usuarioLogado.getPapel() != Papel.ADMIN) {
            ra.addFlashAttribute("mensagem", "Você só pode editar sua própria conta.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/meuPerfil";
        }

        Usuario usuarioComMesmoNome = usuarioRepository.findByUsername(username).orElse(null);
        if (usuarioComMesmoNome != null && !usuarioComMesmoNome.getId().equals(id)) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarUsuario/" + id;
        }

        usuario.setUsername(username);
        if (usuarioLogado != null && usuarioLogado.getPapel() == Papel.ADMIN && papel != null) {
            usuario.setPapel(papel);
        }
        if (!password.isBlank()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

        try {
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                usuario.setFotoPerfil(usuarioService.salvarFoto(fotoPerfil));
            }
        } catch (IOException e) {
            ra.addFlashAttribute("mensagem", "Erro ao salvar a foto!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarUsuario/" + id;
        }

        usuarioRepository.save(usuario);
        ra.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return usuarioLogado != null && usuarioLogado.getPapel() == Papel.ADMIN
                ? "redirect:/listaUsuarios"
                : "redirect:/meuPerfil";
    }

    @GetMapping("/excluirUsuario/{id}")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            ra.addFlashAttribute("mensagem", "Usuário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaUsuarios";
        }
        usuarioRepository.delete(usuario);
        ra.addFlashAttribute("mensagem","Usuário excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaUsuarios";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

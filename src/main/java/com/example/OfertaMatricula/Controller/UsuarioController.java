package com.example.OfertaMatricula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/usuario")
    public String cadastroUsuarios(Model model) {
        model.addAttribute("papeis", Papel.values());
        return "formularioUsuario.html";
    }

    @PostMapping("/cadastrarUsuarios")
    public String saveUsuarios(@RequestParam String login, @RequestParam String senhaHash, @RequestParam Papel papel,
            @RequestParam(required = false) String fotoPerfil, RedirectAttributes ra) {
        if (usuarioRepository.findByLogin(login).isPresent()) {
            ra.addFlashAttribute("mensagem", "Esse login já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("login", login);
            ra.addFlashAttribute("papel", papel);
            ra.addFlashAttribute("fotoPerfil", fotoPerfil);
            return "redirect:/usuario";
        }
        Usuario usuario = new Usuario(login, senhaHash, papel, fotoPerfil);
        usuarioRepository.save(usuario);
        ra.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
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
    public String editarUsuario(@PathVariable long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("papeis", Papel.values());
        return "editarUsuario.html";
    }

    @PostMapping("/atualizarUsuario")
    public String atualizarUsuario(
            @RequestParam long id,
            @RequestParam String login,
            @RequestParam String senhaHash,
            @RequestParam Papel papel,
            @RequestParam(required = false) String fotoPerfil,
            RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            ra.addFlashAttribute("mensagem", "Usuário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaUsuarios";
        }
        Usuario usuarioComMesmoLogin = usuarioRepository.findByLogin(login).orElse(null);
        if (usuarioComMesmoLogin != null && usuarioComMesmoLogin.getId() != id) {
            ra.addFlashAttribute("mensagem", "Esse login já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarUsuario/" + id;
        }
        usuario.setLogin(login);
        usuario.setSenhaHash(senhaHash);
        usuario.setPapel(papel);
        usuario.setFotoPerfil(fotoPerfil);
        usuarioRepository.save(usuario);
        ra.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaUsuarios";
    }

    @GetMapping("/excluirUsuario/{id}")
    public String excluirUsuario(@PathVariable long id, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            ra.addFlashAttribute("mensagem", "Usuário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaUsuarios";
        }
        usuarioRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaUsuarios";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/entrar")
    public String entrar(@RequestParam String login,@RequestParam String senhaHash,RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);
        if (usuario == null || !usuario.getSenhaHash().equals(senhaHash)) {
            ra.addFlashAttribute("mensagem", "Login ou senha inválidos!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("login", login);
            return "redirect:/login";
        }
        return "redirect:/index";
    }
}

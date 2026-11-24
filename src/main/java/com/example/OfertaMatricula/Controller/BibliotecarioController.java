package com.example.OfertaMatricula.Controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Bibliotecario;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.BibliotecarioRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Controller
public class BibliotecarioController {

    @Autowired
    private BibliotecarioRepository bibliotecarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/bibliotecario")
    public String cadastroBibliotecarios() {
        return "formularioBibliotecario.html";
    }

    @PostMapping("/cadastrarBibliotecarios")
    public String saveBibliotecarios(
            @RequestParam String username,
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String endereco,
            RedirectAttributes ra) {

        if (username == null || username.isBlank()) {
            ra.addFlashAttribute("mensagem", "Informe o nome de usuário do bibliotecário!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/bibliotecario";
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/bibliotecario";
        }

        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/bibliotecario";
        }

        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpfFormatado);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/bibliotecario";
        }

        Bibliotecario bibliotecario = new Bibliotecario(nome, cpfFormatado, email, telefoneFormatado, endereco);
        bibliotecarioRepository.save(bibliotecario);

        Usuario usuario = new Usuario(username, "", Papel.BIBLIOTECARIO, null, bibliotecario);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("mensagem", "Bibliotecário cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaBibliotecarios";
    }

    @GetMapping("/listaBibliotecarios")
    public String listaBibliotecarios(Model model, Principal principal) {
        List<Bibliotecario> listaBibliotecarios = bibliotecarioRepository.findAll();
        model.addAttribute("listaBibliotecarios", listaBibliotecarios);
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        return "listaBibliotecarios.html";
    }

    @GetMapping("/editarBibliotecario/{id}")
    public String editarBibliotecario(@PathVariable long id, Model model) {
        Bibliotecario bibliotecario = bibliotecarioRepository.findById(id).orElse(null);
        model.addAttribute("bibliotecario", bibliotecario);
        return "editarBibliotecario.html";
    }

    @PostMapping("/atualizarBibliotecario")
    public String atualizarBibliotecario(
            @RequestParam long id,
            @RequestParam String username,
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String endereco,
            RedirectAttributes ra) {

        Bibliotecario bibliotecario = bibliotecarioRepository.findById(id).orElse(null);
        if (bibliotecario == null) {
            ra.addFlashAttribute("mensagem", "Bibliotecário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaBibliotecarios";
        }

        Usuario usuarioExistente = usuarioRepository.findByUsername(username).orElse(null);
        if (usuarioExistente != null && usuarioExistente.getPessoa() != null
                && !usuarioExistente.getPessoa().getId().equals(bibliotecario.getId())) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarBibliotecario/" + id;
        }

        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarBibliotecario/" + id;
        }

        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarBibliotecario/" + id;
        }

        bibliotecario.setNome(nome);
        bibliotecario.setCpf(cpfFormatado);
        bibliotecario.setEmail(email);
        bibliotecario.setTelefone(telefoneFormatado);
        bibliotecario.setEndereco(endereco);
        bibliotecarioRepository.save(bibliotecario);

        Usuario usuario = bibliotecario.getUsuario();
        if (usuario == null) {
            usuario = new Usuario(username, passwordEncoder.encode(""), Papel.BIBLIOTECARIO, null, bibliotecario);
        } else {
            usuario.setUsername(username);
            usuario.setPapel(Papel.BIBLIOTECARIO);
            usuario.setPessoa(bibliotecario);
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword("");
            }
        }
        bibliotecario.setUsuario(usuario);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("mensagem", "Bibliotecário atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaBibliotecarios";
    }

    @GetMapping("/excluirBibliotecario/{id}")
    @Transactional
    public String excluirBibliotecario(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Usuario usuarioLogado = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;
        if (usuarioLogado != null && usuarioLogado.getPessoa() != null
                && usuarioLogado.getPessoa().getId().equals(id)) {
            ra.addFlashAttribute("mensagem", "Você não pode excluir seu próprio cadastro!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaBibliotecarios";
        }

        Bibliotecario bibliotecario = bibliotecarioRepository.findById(id).orElse(null);
        if (bibliotecario == null) {
            ra.addFlashAttribute("mensagem", "Bibliotecário não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaBibliotecarios";
        }

        Usuario usuario = bibliotecario.getUsuario();
        if (usuario != null) {
            usuario.setPessoa(null);
            bibliotecario.setUsuario(null);
            usuarioRepository.delete(usuario);
        }

        bibliotecarioRepository.delete(bibliotecario);
        ra.addFlashAttribute("mensagem", "Bibliotecário excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaBibliotecarios";
    }
}

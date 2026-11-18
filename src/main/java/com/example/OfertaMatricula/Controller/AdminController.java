package com.example.OfertaMatricula.Controller;

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

import com.example.OfertaMatricula.Model.Admin;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.AdminRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin")
    public String cadastroAdmins() {
        return "formularioAdmin.html";
    }

    @PostMapping("/cadastrarAdmins")
    public String saveAdmins(
            @RequestParam String username,
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String endereco,
            RedirectAttributes ra) {

        if (username == null || username.isBlank()) {
            ra.addFlashAttribute("mensagem", "Informe o nome de usuário do administrador!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/admin";
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            return "redirect:/admin";
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
            return "redirect:/admin";
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
            return "redirect:/admin";
        }

        Admin admin = new Admin(nome, cpfFormatado, email, telefoneFormatado, endereco);
        adminRepository.save(admin);

        Usuario usuario = new Usuario(username, "", Papel.ADMIN, null, admin);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("mensagem", "Administrador cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAdmins";
    }

    @GetMapping("/listaAdmins")
    public String listaAdmins(Model model) {
        List<Admin> listaAdmins = adminRepository.findAll();
        model.addAttribute("listaAdmins", listaAdmins);
        return "listaAdmins.html";
    }

    @GetMapping("/editarAdmin/{id}")
    public String editarAdmin(@PathVariable long id, Model model) {
        Admin admin = adminRepository.findById(id).orElse(null);
        model.addAttribute("admin", admin);
        return "editarAdmin.html";
    }

    @PostMapping("/atualizarAdmin")
    public String atualizarAdmin(
            @RequestParam long id,
            @RequestParam String username,
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String endereco,
            RedirectAttributes ra) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            ra.addFlashAttribute("mensagem", "Administrador não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAdmins";
        }

        Usuario usuarioExistente = usuarioRepository.findByUsername(username).orElse(null);
        if (usuarioExistente != null && usuarioExistente.getPessoa() != null
                && !usuarioExistente.getPessoa().getId().equals(admin.getId())) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarAdmin/" + id;
        }

        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarAdmin/" + id;
        }

        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/editarAdmin/" + id;
        }

        admin.setNome(nome);
        admin.setCpf(cpfFormatado);
        admin.setEmail(email);
        admin.setTelefone(telefoneFormatado);
        admin.setEndereco(endereco);
        adminRepository.save(admin);

        Usuario usuario = admin.getUsuario();
        if (usuario == null) {
            usuario = new Usuario(username, passwordEncoder.encode(""), Papel.ADMIN, null, admin);
        } else {
            usuario.setUsername(username);
            usuario.setPapel(Papel.ADMIN);
            usuario.setPessoa(admin);
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword("");
            }
        }
        admin.setUsuario(usuario);
        usuarioRepository.save(usuario);
        ra.addFlashAttribute("mensagem", "Administrador atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAdmins";
    }

    @GetMapping("/excluirAdmin/{id}")
    public String excluirAdmin(@PathVariable long id, RedirectAttributes ra) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            ra.addFlashAttribute("mensagem", "Administrador não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAdmins";
        }

        if (admin.getUsuario() != null) {
            usuarioRepository.delete(admin.getUsuario());
        }

        adminRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Administrador excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAdmins";
    }
}

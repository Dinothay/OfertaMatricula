package com.example.OfertaMatricula.Controller;

import java.security.Principal;
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
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.ProvarRepository;

import jakarta.transaction.Transactional;

@Controller
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaDisciplinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ProvarRepository provarRepository;

    @GetMapping("/professor")
    public String cadastroProfessores() {
        return "formularioProfessor.html";
    }

    @PostMapping("/cadastrarProfessores")
    public String saveProfessores(@RequestParam String username, @RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
            @RequestParam String telefone, @RequestParam String endereco, @RequestParam String area,
            @RequestParam String formacao, @RequestParam Double salario, RedirectAttributes ra) {
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            ra.addFlashAttribute("salario", salario);
            return "redirect:/professor";
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            ra.addFlashAttribute("salario", salario);
            return "redirect:/professor";
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;

        if (username == null || username.isBlank()) {
            ra.addFlashAttribute("mensagem", "Informe o nome de usuário do professor!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            ra.addFlashAttribute("salario", salario);
            return "redirect:/professor";
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            ra.addFlashAttribute("salario", salario);
            return "redirect:/professor";
        }

        Professor professor = new Professor(nome, cpf, email, telefone, area, formacao, endereco, salario);
        professorRepository.save(professor);

        Usuario usuario = new Usuario(username, null, Papel.PROFESSOR, null, professor);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("mensagem", "Professor cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProfessores";
    }

    @GetMapping("/listaProfessores")
    public String listaProfessores(Model model, Principal principal) {
        List<Professor> listaProfessores = professorRepository.findAll();
        model.addAttribute("listaProfessores", listaProfessores);
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        return "listaProfessores.html";
    }

    @GetMapping("/editarProfessor/{id}")
    public String editarProfessor(@PathVariable long id, Model model, Principal principal) {
        Professor professor = professorRepository.findById(id).orElse(null);
        model.addAttribute("professor", professor);

        boolean professorEditandoProprioPerfil = false;
        if (principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsername(principal.getName()).orElse(null);
            if (usuarioLogado != null && usuarioLogado.getPapel() == Papel.PROFESSOR
                    && usuarioLogado.getPessoa() instanceof Professor professorLogado
                    && professor != null
                    && professorLogado.getId().equals(professor.getId())) {
                professorEditandoProprioPerfil = true;
            }
        }
        model.addAttribute("professorEditandoProprioPerfil", professorEditandoProprioPerfil);
        return "editarProfessor.html";
    }

    @PostMapping("/atualizarProfessor")
    public String atualizarProfessor(@RequestParam long id, @RequestParam String nome, @RequestParam String cpf,
            @RequestParam String email, @RequestParam String telefone,
            @RequestParam(required = false) Double salario,
            @RequestParam String endereco, @RequestParam String area, @RequestParam String formacao,
            RedirectAttributes ra, Principal principal) {
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("salario", salario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            return "redirect:/editarProfessor/" + id;
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("salario", salario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("area", area);
            ra.addFlashAttribute("formacao", formacao);
            return "redirect:/editarProfessor/" + id;
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor == null) {
            ra.addFlashAttribute("mensagem", "Professor não encontrado!");
            return "redirect:/listaProfessores";
        }
        professor.setNome(nome);
        professor.setCpf(cpf);
        professor.setEmail(email);
        professor.setTelefone(telefone);

        boolean professorEditandoProprioPerfil = false;
        if (principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsername(principal.getName()).orElse(null);
            if (usuarioLogado != null && usuarioLogado.getPapel() == Papel.PROFESSOR
                    && usuarioLogado.getPessoa() instanceof Professor professorLogado
                    && professorLogado.getId().equals(professor.getId())) {
                professorEditandoProprioPerfil = true;
            }
        }

        if (!professorEditandoProprioPerfil && salario != null) {
            professor.setSalario(salario);
        }
        professor.setEndereco(endereco);
        professor.setArea(area);
        professor.setFormacao(formacao);
        professorRepository.save(professor);
        ra.addFlashAttribute("mensagem", "Professor atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProfessores";
    }

    @GetMapping("/excluirProfessor/{id}")
    @Transactional
    public String excluirProfessor(
            @PathVariable long id,
            Principal principal,
            RedirectAttributes ra) {
        Usuario usuarioLogado = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;
        if (usuarioLogado != null && usuarioLogado.getPessoa() != null
                && usuarioLogado.getPessoa().getId().equals(id)) {
            ra.addFlashAttribute("mensagem", "Você não pode excluir seu próprio cadastro!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }

        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor == null) {
            ra.addFlashAttribute("mensagem", "Professor não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }

        if (ofertaDisciplinaRepository.existsByProfessorId(id)) {
            ra.addFlashAttribute(
                    "mensagem",
                    "Não é possível excluir: professor está associado a uma oferta de disciplina.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }

        if (provarRepository.existsByProfessorId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: professor está associado a uma prova.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }
        if (emprestimoRepository.existsByPessoaId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: professor está associado a um empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }
        if (!reservaRepository.findByPessoaId(id).isEmpty()) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: professor está associado a uma reserva.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProfessores";
        }

        Usuario usuario = professor.getUsuario();
        if (usuario != null) {
            usuario.setPessoa(null);
            professor.setUsuario(null);
            usuarioRepository.delete(usuario);
        }

        professorRepository.delete(professor);
        ra.addFlashAttribute("mensagem", "Professor excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProfessores";
    }
}

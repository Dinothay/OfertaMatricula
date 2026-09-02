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

import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;

@Controller
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaDisciplinaRepository;

    @GetMapping("/professor")
    public String cadastroProfessores() {
        return "formularioProfessor.html";
    }

    @PostMapping("/cadastrarProfessores")
    public String saveProfessores(@RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
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
        professorRepository.save(new Professor(nome, cpf, email, telefone, area, formacao, endereco, salario));
        ra.addFlashAttribute("mensagem", "Professor cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProfessores";
    }

    @GetMapping("/listaProfessores")
    public String listaProfessores(Model model) {
        List<Professor> listaProfessores = professorRepository.findAll();

        model.addAttribute("listaProfessores", listaProfessores);

        return "listaProfessores.html";
    }

    @GetMapping("/editarProfessor/{id}")
    public String editarProfessor(@PathVariable long id, Model model) {

        Professor professor = professorRepository.findById(id).orElse(null);

        model.addAttribute("professor", professor);

        return "editarProfessor.html";
    }

    @PostMapping("/atualizarProfessor")
    public String atualizarProfessor(@RequestParam long id, @RequestParam String nome, @RequestParam String cpf,
            @RequestParam String email, @RequestParam String telefone, @RequestParam Double salario,
            @RequestParam String endereco, @RequestParam String area, @RequestParam String formacao,
            RedirectAttributes ra) {
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
        professor.setSalario(salario);
        professor.setEndereco(endereco);
        professor.setArea(area);
        professor.setFormacao(formacao);

        professorRepository.save(professor);
        ra.addFlashAttribute("mensagem", "Professor atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProfessores";
    }

    @GetMapping("/excluirProfessor/{id}")
    public String excluirProfessor(
            @PathVariable long id,
            RedirectAttributes ra) {

        if (ofertaDisciplinaRepository.existsByProfessorId(id)) {
            ra.addFlashAttribute(
                    "mensagem",
                    "Não é possível excluir: professor está associado a uma oferta de disciplina.");

            return "redirect:/listaProfessores";
        }

        professorRepository.deleteById(id);

        return "redirect:/listaProfessores";
    }
}

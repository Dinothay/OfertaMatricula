package com.example.OfertaMatricula.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;

@Controller
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @GetMapping("/aluno")
    public String cadastroAlunos() {
        return "formularioAluno.html";
    }

    @PostMapping("/cadastrarAlunos")
    public String saveAlunos(@RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
            @RequestParam String telefone, @RequestParam int prontuario, @RequestParam String endereco,
            @RequestParam LocalDate dataInicio, RedirectAttributes ra) {
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataInicio);
            return "redirect:/aluno";
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataInicio);
            return "redirect:/aluno";
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;
        alunoRepository.save(new Aluno(nome, cpf, email, telefone, prontuario, endereco, dataInicio));
        ra.addFlashAttribute("mensagem", "Aluno cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAlunos";
    }

    @GetMapping("/listaAlunos")
    public String listaAlunos(Model model) {
        List<Aluno> listaAlunos = alunoRepository.findAll();
        model.addAttribute("listaAlunos", listaAlunos);
        return "listaAlunos.html";
    }

    @GetMapping("/editarAluno/{id}")
    public String editarAluno(@PathVariable long id, Model model) {
        Aluno aluno = alunoRepository.findById(id).orElse(null);
        model.addAttribute("aluno", aluno);
        return "editarAluno.html";
    }

    @PostMapping("/atualizarAluno")
    public String atualizarAluno(
            @RequestParam long id,
            @RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
            @RequestParam String telefone, @RequestParam int prontuario, @RequestParam String endereco,
            @RequestParam LocalDate dataInicio, RedirectAttributes ra) {
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataInicio);
            return "redirect:/editarAluno/" + id;
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataInicio);
            return "redirect:/editarAluno/" + id;
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;
        Aluno aluno = alunoRepository.findById(id).orElse(null);
        if (aluno == null) {
            ra.addFlashAttribute("mensagem", "Aluno não encontrado!");
            return "redirect:/listaAlunos";
        }
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setEmail(email);
        aluno.setTelefone(telefone);
        aluno.setProntuario(prontuario);
        aluno.setEndereco(endereco);
        aluno.setDataInicio(dataInicio);
        alunoRepository.save(aluno);
        return "redirect:/listaAlunos";
    }

    @GetMapping("/excluirAluno/{id}")
    public String excluirAluno(
            @PathVariable long id,
            RedirectAttributes ra) {
        if (matriculaRepository.existsByAlunoId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: aluno está associado a uma matrícula.");
            return "redirect:/listaAlunos";
        }
        alunoRepository.deleteById(id);
        return "redirect:/listaAlunos";
    }
}

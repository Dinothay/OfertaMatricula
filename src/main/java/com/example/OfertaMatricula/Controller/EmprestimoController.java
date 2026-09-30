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
import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;

@Controller
public class EmprestimoController {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/emprestimo")
    public String cadastroEmprestimo(Model model) {
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "formularioEmprestimo.html";
    }

    @PostMapping("/cadastraEmprestimo")
    public String saveEmprestimo(@RequestParam Long idAluno, @RequestParam Long idLivro,
            @RequestParam LocalDate dataRetirada, @RequestParam(required = false) LocalDate dataPrevista,
            @RequestParam(required = false) LocalDate dataDevolucao, @RequestParam StatusEmprestimo status,
            @RequestParam int renovacoes, RedirectAttributes ra) {
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Livro livro = livroRepository.findById(idLivro).orElseThrow();
        LocalDate devolucaoPrevista = calcularDataPrevista(dataRetirada, dataPrevista);
        emprestimoRepository.save(new Emprestimo(aluno, livro, dataRetirada, devolucaoPrevista, dataDevolucao, status, renovacoes));
        ra.addFlashAttribute("mensagem", "Empréstimo cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    @GetMapping("/listaEmprestimo")
    public String listaEmprestimo(Model model) {
        List<Emprestimo> listaEmprestimo = emprestimoRepository.findAll();
        model.addAttribute("listaEmprestimo", listaEmprestimo);
        return "listaEmprestimo.html";
    }

    @GetMapping("/editarEmprestimo/{id}")
    public String editarEmprestimo(@PathVariable long id, Model model) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        model.addAttribute("emprestimo", emprestimo);
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "editarEmprestimo.html";
    }

    @PostMapping("/atualizarEmprestimo")
    public String atualizarEmprestimo(@RequestParam long id, @RequestParam Long idAluno, @RequestParam Long idLivro,
            @RequestParam LocalDate dataRetirada, @RequestParam(required = false) LocalDate dataPrevista,
            @RequestParam(required = false) LocalDate dataDevolucao, @RequestParam StatusEmprestimo status,
            @RequestParam int renovacoes, RedirectAttributes ra) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        if (emprestimo == null) {
            return "redirect:/listaEmprestimo";
        }

        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Livro livro = livroRepository.findById(idLivro).orElseThrow();
        LocalDate devolucaoPrevista = calcularDataPrevista(dataRetirada, dataPrevista);

        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevista(devolucaoPrevista);
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatus(status);
        emprestimo.setRenovacoes(renovacoes);
        emprestimoRepository.save(emprestimo);
        ra.addFlashAttribute("mensagem", "Empréstimo atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    @GetMapping("/excluirEmprestimo/{id}")
    public String excluirEmprestimo(@PathVariable long id, RedirectAttributes ra) {
        emprestimoRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Empréstimo excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    private LocalDate calcularDataPrevista(LocalDate dataRetirada, LocalDate dataPrevista) {
        if (dataPrevista != null && !dataPrevista.isBefore(dataRetirada)) {
            return dataPrevista;
        }
        return dataRetirada.plusDays(14);
    }
}

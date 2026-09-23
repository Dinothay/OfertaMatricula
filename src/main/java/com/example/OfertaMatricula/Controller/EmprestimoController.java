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

import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;

@Controller
public class EmprestimoController {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping("/emprestimo")
    public String cadastroEmprestimo() {
        return "formularioEmprestimo.html";
    }

    @PostMapping("/cadastraEmprestimo")
    public String saveEmprestimo(@RequestParam LocalDate dataRetirada, @RequestParam LocalDate dataPrevista,
            @RequestParam(required = false) LocalDate dataDevolucao, @RequestParam String status,
            @RequestParam int renovacoes) {
        emprestimoRepository.save(new Emprestimo(dataRetirada, dataPrevista, dataDevolucao, status, renovacoes));
        return "redirect:/emprestimo";
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
        return "editarEmprestimo.html";
    }

    @PostMapping("/atualizarEmprestimo")
    public String atualizarEmprestimo(@RequestParam long id, @RequestParam LocalDate dataRetirada,
            @RequestParam LocalDate dataPrevista, @RequestParam(required = false) LocalDate dataDevolucao,
            @RequestParam String status, @RequestParam int renovacoes) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevista(dataPrevista);
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatus(status);
        emprestimo.setRenovacoes(renovacoes);
        emprestimoRepository.save(emprestimo);
        return "redirect:/listaEmprestimo";
    }

    @GetMapping("/excluirEmprestimo/{id}")
    public String excluirEmprestimo(@PathVariable long id) {
        emprestimoRepository.deleteById(id);
        return "redirect:/listaEmprestimo";
    }
}

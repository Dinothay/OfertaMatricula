package com.example.OfertaMatricula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Exemplar;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.ExemplarRepository;

@Controller
public class ExemplarController {
    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping("/exemplar")
    public String cadastroExemplar(Model model) {
        model.addAttribute("emprestimos", emprestimoRepository.findAll());
        return "formularioExemplar.html";
    }

    @PostMapping("/cadastraExemplar")
    public String saveExemplar(@RequestParam String tombo, @RequestParam Long idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow();
        exemplarRepository.save(new Exemplar(tombo, emprestimo));
        return "redirect:/exemplar";
    }

    @GetMapping("/listaExemplar")
    public String listaExemplar(Model model) {
        List<Exemplar> listaExemplar = exemplarRepository.findAll();
        model.addAttribute("listaExemplar", listaExemplar);
        return "listaExemplar.html";
    }

    @GetMapping("/editarExemplar/{id}")
    public String editarExemplar(@PathVariable long id, Model model) {
        Exemplar exemplar = exemplarRepository.findById(id).orElse(null);
        model.addAttribute("exemplar", exemplar);
        model.addAttribute("emprestimos", emprestimoRepository.findAll());
        return "editarExemplar.html";
    }

    @PostMapping("/atualizarExemplar")
    public String atualizarExemplar(@RequestParam long id, @RequestParam String tombo,
            @RequestParam Long idEmprestimo) {
        Exemplar exemplar = exemplarRepository.findById(id).orElse(null);
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow();
        exemplar.setTombo(tombo);
        exemplar.setStatus(emprestimo);
        exemplarRepository.save(exemplar);
        return "redirect:/listaExemplar";
    }

    @GetMapping("/excluirExemplar/{id}")
    public String excluirExemplar(@PathVariable long id) {
        exemplarRepository.deleteById(id);
        return "redirect:/listaExemplar";
    }
}

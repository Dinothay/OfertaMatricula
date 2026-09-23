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

import com.example.OfertaMatricula.Model.Prova;
import com.example.OfertaMatricula.Repository.ProvarRepository;

@Controller
public class ProvaController {
    @Autowired
    private ProvarRepository provarRepository;

    @GetMapping("/prova")
    public String cadastroProva() {
        return "formularioProva.html";
    }

    @PostMapping("/cadastraProva")
    public String saveProva(@RequestParam LocalDate data, @RequestParam String conteudo,
            @RequestParam double peso) {
        provarRepository.save(new Prova(data, conteudo, peso));
        return "redirect:/prova";
    }

    @GetMapping("/listaProva")
    public String listaProva(Model model) {
        List<Prova> listaProva = provarRepository.findAll();
        model.addAttribute("listaProva", listaProva);
        return "listaProva.html";
    }

    @GetMapping("/editarProva/{id}")
    public String editarProva(@PathVariable long id, Model model) {
        Prova prova = provarRepository.findById(id).orElse(null);
        model.addAttribute("prova", prova);
        return "editarProva.html";
    }

    @PostMapping("/atualizarProva")
    public String atualizarProva(@RequestParam long id, @RequestParam LocalDate data,
            @RequestParam String conteudo, @RequestParam double peso) {
        Prova prova = provarRepository.findById(id).orElse(null);
        prova.setData(data);
        prova.setConteudo(conteudo);
        prova.setPeso(peso);
        provarRepository.save(prova);
        return "redirect:/listaProva";
    }

    @GetMapping("/excluirProva/{id}")
    public String excluirProva(@PathVariable long id) {
        provarRepository.deleteById(id);
        return "redirect:/listaProva";
    }
}

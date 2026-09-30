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
            @RequestParam double peso, RedirectAttributes ra) {
        provarRepository.save(new Prova(data, conteudo, peso));
        ra.addFlashAttribute("mensagem", "Prova cadastrada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
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
            @RequestParam String conteudo, @RequestParam double peso, RedirectAttributes ra) {
        Prova prova = provarRepository.findById(id).orElse(null);
        if (prova == null) {
            ra.addFlashAttribute("mensagem", "Prova não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProva";
        }
        prova.setData(data);
        prova.setConteudo(conteudo);
        prova.setPeso(peso);
        provarRepository.save(prova);
        ra.addFlashAttribute("mensagem", "Prova atualizada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
    }

    @GetMapping("/excluirProva/{id}")
    public String excluirProva(@PathVariable long id, RedirectAttributes ra) {
        if (!provarRepository.existsById(id)) {
            ra.addFlashAttribute("mensagem", "Prova não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProva";
        }
        provarRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Prova excluída com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
    }
}

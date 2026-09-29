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

import com.example.OfertaMatricula.Model.Exemplar;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.StatusExemplar;
import com.example.OfertaMatricula.Repository.ExemplarRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;

@Controller
public class ExemplarController {

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/exemplar")
    public String cadastroExemplar(Model model) {

        model.addAttribute("livros", livroRepository.findAll());

        return "formularioExemplar.html";
    }

    @PostMapping("/cadastraExemplar")
    public String saveExemplar(
            @RequestParam String tombo,
            @RequestParam Long idLivro,
            @RequestParam StatusExemplar status,
            RedirectAttributes ra) {

        Livro livro = livroRepository.findById(idLivro).orElse(null);

        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/exemplar";
        }

        exemplarRepository.save(new Exemplar(tombo, status, livro));

        ra.addFlashAttribute("mensagem", "Exemplar cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return "redirect:/listaExemplar";
    }

    @GetMapping("/listaExemplar")
    public String listaExemplar(Model model) {

        List<Exemplar> listaExemplar = exemplarRepository.findAll();

        model.addAttribute("listaExemplar", listaExemplar);

        return "listaExemplar.html";
    }

    @GetMapping("/editarExemplar/{id}")
    public String editarExemplar(
            @PathVariable long id,
            Model model) {

        Exemplar exemplar = exemplarRepository.findById(id).orElse(null);

        model.addAttribute("exemplar", exemplar);
        model.addAttribute("livros", livroRepository.findAll());

        return "editarExemplar.html";
    }

    @PostMapping("/atualizarExemplar")
    public String atualizarExemplar(
            @RequestParam long id,
            @RequestParam String tombo,
            @RequestParam Long idLivro,
            @RequestParam StatusExemplar status,
            RedirectAttributes ra) {

        Exemplar exemplar = exemplarRepository.findById(id).orElse(null);

        if (exemplar == null) {
            ra.addFlashAttribute("mensagem", "Exemplar não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaExemplar";
        }

        Livro livro = livroRepository.findById(idLivro).orElse(null);

        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaExemplar";
        }

        exemplar.setTombo(tombo);
        exemplar.setStatus(status);
        exemplar.setLivro(livro);

        exemplarRepository.save(exemplar);

        ra.addFlashAttribute("mensagem", "Exemplar atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return "redirect:/listaExemplar";
    }

    @GetMapping("/excluirExemplar/{id}")
    public String excluirExemplar(
            @PathVariable long id,
            RedirectAttributes ra) {

        exemplarRepository.deleteById(id);

        ra.addFlashAttribute("mensagem", "Exemplar excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return "redirect:/listaExemplar";
    }
}
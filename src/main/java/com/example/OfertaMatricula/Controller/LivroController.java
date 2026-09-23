package com.example.OfertaMatricula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Repository.LivroRepository;

@Controller
public class LivroController {
    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/livro")
    public String cadastroLivro() {
        return "formularioLivro.html";
    }

    @PostMapping("/cadastraLivro")
    public String saveLivro(@RequestParam String titulo, @RequestParam String autor, @RequestParam String isbn,
            @RequestParam String edicao, @RequestParam String capalmagem) {
        livroRepository.save(new Livro(titulo, autor, isbn, edicao, capalmagem));
        return "redirect:/livro";
    }

    @GetMapping("/listaLivro")
    public String listaLivro(Model model) {
        List<Livro> listaLivro = livroRepository.findAll();
        model.addAttribute("listaLivro", listaLivro);
        return "listaLivro.html";
    }

    @GetMapping("/editarLivro/{id}")
    public String editarLivro(@PathVariable long id, Model model) {
        Livro livro = livroRepository.findById(id).orElse(null);
        model.addAttribute("livro", livro);
        return "editarLivro.html";
    }

    @PostMapping("/atualizarLivro")
    public String atualizarLivro(@RequestParam long id, @RequestParam String titulo, @RequestParam String autor,
            @RequestParam String isbn, @RequestParam String edicao, @RequestParam String capalmagem) {
        Livro livro = livroRepository.findById(id).orElse(null);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setEdicao(edicao);
        livro.setCapalmagem(capalmagem);
        livroRepository.save(livro);
        return "redirect:/listaLivro";
    }

    @GetMapping("/excluirLivro/{id}")
    public String excluirLivro(@PathVariable long id) {
        livroRepository.deleteById(id);
        return "redirect:/listaLivro";
    }
}

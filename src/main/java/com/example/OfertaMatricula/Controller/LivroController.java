package com.example.OfertaMatricula.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Repository.DisciplinaLivroRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Service.LivroService;

@Controller
public class LivroController {
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private LivroService livroService;
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private DisciplinaLivroRepository disciplinaLivroRepository;

    @GetMapping("/livro")
    public String cadastroLivro() {
        return "formularioLivro.html";
    }

    @PostMapping("/cadastraLivro")
    public String saveLivro(@RequestParam String titulo, @RequestParam String autor, @RequestParam String isbn,
            @RequestParam String edicao, @RequestParam("capalmagem") MultipartFile capalmagem, RedirectAttributes ra) throws IOException {
        String caminho = livroService.salvarLogo(capalmagem);
        livroRepository.save(new Livro(titulo, autor, isbn, edicao, caminho));
        ra.addFlashAttribute("mensagem", "Livro cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
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
            @RequestParam String isbn, @RequestParam String edicao,
            @RequestParam(value = "capalmagem", required = false) MultipartFile capalmagem, RedirectAttributes ra) throws IOException {
        Livro livro = livroRepository.findById(id).orElse(null);
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setEdicao(edicao);
        if (capalmagem != null && !capalmagem.isEmpty()) {
            System.out.println("NOVA CAPA: " + capalmagem.getOriginalFilename());
            String caminho = livroService.salvarLogo(capalmagem);
            livro.setCapalmagem(caminho);
        }
        livroRepository.save(livro);
        ra.addFlashAttribute("mensagem", "Livro atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @GetMapping("/excluirLivro/{id}")
    public String excluirLivro(@PathVariable long id, RedirectAttributes ra) {
        if (emprestimoRepository.existsByLivroId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: livro está associado a um empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        if (disciplinaLivroRepository.existsByLivroId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: livro está associado a uma disciplina.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        livroRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Livro excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }
}

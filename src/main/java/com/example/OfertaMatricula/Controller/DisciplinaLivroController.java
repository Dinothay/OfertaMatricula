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

import com.example.OfertaMatricula.Model.Disciplina;
import com.example.OfertaMatricula.Model.DisciplinaLivro;
import com.example.OfertaMatricula.Model.DisciplinaLivroId;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Tipo;
import com.example.OfertaMatricula.Repository.DisciplinaLivroRepository;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;

@Controller
public class DisciplinaLivroController {

    @Autowired
    private DisciplinaLivroRepository disciplinaLivroRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/disciplinaLivro")
    public String cadastroDisciplinaLivro(Model model) {

        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());

        return "formularioDisciplinaLivro.html";
    }

    @PostMapping("/cadastrarDisciplinaLivro")
    public String cadastrarDisciplinaLivro(
            @RequestParam long disciplinaId,
            @RequestParam long livroId,
            @RequestParam Tipo tipo,
            RedirectAttributes ra) {

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId).orElse(null);

        if (disciplina == null) {
            ra.addFlashAttribute("mensagem", "Disciplina não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/disciplinaLivro";
        }

        Livro livro = livroRepository.findById(livroId).orElse(null);

        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/disciplinaLivro";
        }

        DisciplinaLivro disciplinaLivro = new DisciplinaLivro(disciplina, livro, tipo);

        disciplinaLivroRepository.save(disciplinaLivro);

        ra.addFlashAttribute("mensagem",
                "Livro associado à disciplina com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return "redirect:/listaDisciplinaLivro";
    }

    @GetMapping("/listaDisciplinaLivro")
    public String listaDisciplinaLivro(Model model) {

        List<DisciplinaLivro> listaDisciplinaLivro = disciplinaLivroRepository.findAll();

        model.addAttribute("listaDisciplinaLivro", listaDisciplinaLivro);

        return "listaDisciplinaLivro.html";
    }

    @GetMapping("/editarDisciplinaLivro/{disciplinaId}/{livroId}")
    public String editarDisciplinaLivro(@PathVariable long disciplinaId,@PathVariable long livroId,Model model) {

        DisciplinaLivro disciplinaLivro = disciplinaLivroRepository.findById(new DisciplinaLivroId(disciplinaId, livroId)).orElse(null);
        model.addAttribute("disciplinaLivro", disciplinaLivro);
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());

        return "editarDisciplinaLivro.html";
    }

    @PostMapping("/atualizarDisciplinaLivro")
    public String atualizarDisciplinaLivro(
            @RequestParam long disciplinaId,
            @RequestParam long livroId,
            @RequestParam Tipo tipo,
            RedirectAttributes ra) {

        DisciplinaLivro disciplinaLivro = disciplinaLivroRepository.findById(new DisciplinaLivroId(disciplinaId, livroId)).orElse(null);

        if (disciplinaLivro == null) {
            ra.addFlashAttribute("mensagem","Associação entre disciplina e livro não encontrada.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaDisciplinaLivro";
        }

        disciplinaLivro.setTipo(tipo);
        disciplinaLivroRepository.save(disciplinaLivro);
        ra.addFlashAttribute("mensagem","Associação atualizada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return "redirect:/listaDisciplinaLivro";
    }

    @GetMapping("/excluirDisciplinaLivro/{disciplinaId}/{livroId}")
    public String excluirDisciplinaLivro(@PathVariable long disciplinaId,@PathVariable long livroId,RedirectAttributes ra) {
        DisciplinaLivroId id = new DisciplinaLivroId(disciplinaId, livroId);
        if (!disciplinaLivroRepository.existsById(id)) {
            ra.addFlashAttribute("mensagem", "Associação entre disciplina e livro não encontrada.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaDisciplinaLivro";
        }
        disciplinaLivroRepository.deleteById(id);
        ra.addFlashAttribute("mensagem","Associação excluída com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaDisciplinaLivro";
    }
}
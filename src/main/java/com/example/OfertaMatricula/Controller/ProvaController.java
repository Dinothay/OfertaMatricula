package com.example.OfertaMatricula.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Model.Prova;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.ProvarRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class ProvaController {
    @Autowired
    private ProvarRepository provarRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/prova")
    public String cadastroProva(Model model) {
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "formularioProva.html";
    }

    @PostMapping("/cadastraProva")
    public String saveProva(@RequestParam LocalDate data, @RequestParam String conteudo,
            @RequestParam double peso, @RequestParam Long disciplinaId,
            @RequestParam(required = false) List<Long> livroIds, Principal principal, RedirectAttributes ra) {
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId).orElse(null);
        List<Livro> livros = livroIds == null ? List.of() : livroRepository.findAllById(livroIds);

        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        Professor professor = usuario != null && usuario.getPessoa() instanceof Professor ? (Professor) usuario.getPessoa() : null;

        Prova prova = new Prova(data, conteudo, peso, disciplina, livros);
        prova.setProfessor(professor);
        provarRepository.save(prova);
        ra.addFlashAttribute("mensagem", "Prova cadastrada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
    }

    @GetMapping("/listaProva")
    public String listaProva(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        List<Prova> listaProva = provarRepository.findAll();
        model.addAttribute("listaProva", listaProva);
        return "listaProva.html";
    }

    @GetMapping("/listaProvaAluno")
    public String listaProvaAluno(Model model, Principal principal) {
        return "redirect:/listaProva";
    }

    @GetMapping("/editarProva/{id}")
    public String editarProva(@PathVariable long id, Model model, Principal principal, RedirectAttributes ra) {
        Prova prova = provarRepository.findById(id).orElse(null);
        if (prova == null) {
            ra.addFlashAttribute("mensagem", "Prova não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProva";
        }

        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario != null && usuario.getPapel() == Papel.PROFESSOR) {
            Professor professorLogado = (Professor) usuario.getPessoa();
            if (prova.getProfessor() == null || !prova.getProfessor().getId().equals(professorLogado.getId())) {
                ra.addFlashAttribute("mensagem", "Você só pode editar provas que criou.");
                ra.addFlashAttribute("mensagemTipo", "erro");
                return "redirect:/meuPainelProfessor";
            }
        }
        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("livroIdsSelecionados", prova.getLivros().stream().map(Livro::getId).toList());
        model.addAttribute("prova", prova);
        model.addAttribute("disciplinas", disciplinaRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "editarProva.html";
    }

    @PostMapping("/atualizarProva")
    public String atualizarProva(@RequestParam long id, @RequestParam LocalDate data,
            @RequestParam String conteudo, @RequestParam double peso,
            @RequestParam Long disciplinaId,
            @RequestParam(required = false) List<Long> livroIds, Principal principal, RedirectAttributes ra) {
        Prova prova = provarRepository.findById(id).orElse(null);
        if (prova == null) {
            ra.addFlashAttribute("mensagem", "Prova não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProva";
        }

        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario != null && usuario.getPapel() == Papel.PROFESSOR) {
            Professor professorLogado = (Professor) usuario.getPessoa();
            if (prova.getProfessor() == null || !prova.getProfessor().getId().equals(professorLogado.getId())) {
                ra.addFlashAttribute("mensagem", "Você só pode editar provas que criou.");
                ra.addFlashAttribute("mensagemTipo", "erro");
                return "redirect:/listaProva";
            }
            prova.setProfessor(professorLogado);
        }

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId).orElse(null);
        List<Livro> livros = livroIds == null
                ? new ArrayList<>()
                : new ArrayList<>(livroRepository.findAllById(livroIds));

        prova.setData(data);
        prova.setConteudo(conteudo);
        prova.setPeso(peso);
        prova.setDisciplina(disciplina);
        prova.setLivros(livros);
        provarRepository.saveAndFlush(prova);
        ra.addFlashAttribute("mensagem", "Prova atualizada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
    }

    @GetMapping("/excluirProva/{id}")
    public String excluirProva(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Prova prova = provarRepository.findById(id).orElse(null);
        if (prova == null) {
            ra.addFlashAttribute("mensagem", "Prova não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaProva";
        }

        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario != null && usuario.getPapel() == Papel.PROFESSOR) {
            Professor professorLogado = (Professor) usuario.getPessoa();
            if (prova.getProfessor() == null || !prova.getProfessor().getId().equals(professorLogado.getId())) {
                ra.addFlashAttribute("mensagem", "Você só pode excluir provas que criou.");
                ra.addFlashAttribute("mensagemTipo", "erro");
                return "redirect:/listaProva";
            }
        }

        provarRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Prova excluída com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaProva";
    }
}

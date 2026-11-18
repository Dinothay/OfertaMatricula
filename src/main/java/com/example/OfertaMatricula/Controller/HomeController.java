package com.example.OfertaMatricula.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Matricula;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Model.Prova;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;
import com.example.OfertaMatricula.Repository.ProvarRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ProvarRepository provarRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping({"/", "/index"})
    public String index(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuarioLogado", usuario);
                if (usuario.getPapel() == Papel.PROFESSOR) {
                    return "redirect:/indexProfessor";
                }
                if (usuario.getPapel() == Papel.BIBLIOTECARIO) {
                    return "redirect:/indexBibliotecario";
                }
            }
        }

        model.addAttribute("totalLivros", livroRepository.count());
        model.addAttribute("totalDisponiveisEmprestimo", livroRepository.countByStatus(StatusLivro.DISPONIVEL));

        return "index.html";
    }

    @GetMapping("/indexAluno")
    public String indexAluno(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }

        model.addAttribute("totalLivros", livroRepository.count());
        model.addAttribute("totalDisponiveisEmprestimo", livroRepository.countByStatus(StatusLivro.DISPONIVEL));

        return "indexAluno.html";
    }

    @GetMapping("/indexProfessor")
    public String indexProfessor(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }

        model.addAttribute("totalLivros", livroRepository.count());
        model.addAttribute("totalDisponiveisEmprestimo", livroRepository.countByStatus(StatusLivro.DISPONIVEL));

        return "indexProfessor.html";
    }

    @GetMapping("/indexBibliotecario")
    public String indexBibliotecario(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }

        model.addAttribute("totalLivros", livroRepository.count());
        model.addAttribute("totalDisponiveisEmprestimo", livroRepository.countByStatus(StatusLivro.DISPONIVEL));

        return "indexBibliotecario.html";
    }

    @GetMapping("/meuPainelAluno")
    public String meuPainelAluno(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogado", usuario);

        if (usuario.getPessoa() != null && usuario.getPessoa().getId() != null) {
            List<Matricula> minhasMatriculas = matriculaRepository.findByAlunoId(usuario.getPessoa().getId());
            List<Emprestimo> meusEmprestimos = emprestimoRepository.findByAlunoId(usuario.getPessoa().getId());
            List<Reserva> minhasReservas = reservaRepository.findByAlunoId(usuario.getPessoa().getId());
            model.addAttribute("minhasMatriculas", minhasMatriculas);
            model.addAttribute("meusEmprestimos", meusEmprestimos);
            model.addAttribute("minhasReservas", minhasReservas);
            model.addAttribute("emprestimosRenovaveisIds", meusEmprestimos.stream()
                    .filter(emprestimo -> emprestimo.getStatus() == com.example.OfertaMatricula.Model.StatusEmprestimo.ATIVO)
                    .filter(emprestimo -> emprestimo.getRenovacoes() < 3)
                    .filter(emprestimo -> reservaRepository.findByLivroIdOrderByDataReservaAsc(emprestimo.getLivro().getId()).isEmpty())
                    .map(Emprestimo::getId)
                    .collect(java.util.stream.Collectors.toSet()));
        }

        return "meuPainelAluno.html";
    }

    @GetMapping("/meuPainelProfessor")
    public String meuPainelProfessor(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogado", usuario);

        if (usuario.getPessoa() instanceof Professor professor) {
            List<Prova> minhasProvas = provarRepository.findByProfessorId(professor.getId());
            List<Emprestimo> meusEmprestimos = emprestimoRepository.findByAlunoId(professor.getId());
            List<Reserva> minhasReservas = reservaRepository.findByAlunoId(professor.getId());

            model.addAttribute("minhasProvas", minhasProvas);
            model.addAttribute("meusEmprestimos", meusEmprestimos);
            model.addAttribute("minhasReservas", minhasReservas);
            model.addAttribute("emprestimosRenovaveisIds", meusEmprestimos.stream()
                    .filter(emprestimo -> emprestimo.getStatus() == com.example.OfertaMatricula.Model.StatusEmprestimo.ATIVO)
                    .filter(emprestimo -> emprestimo.getRenovacoes() < 3)
                    .filter(emprestimo -> reservaRepository.findByLivroIdOrderByDataReservaAsc(emprestimo.getLivro().getId()).isEmpty())
                    .map(Emprestimo::getId)
                    .collect(java.util.stream.Collectors.toSet()));
        }

        return "meuPainelProfessor.html";
    }

    @GetMapping("/biblioteca")
    public String biblioteca() {
        return "biblioteca.html";
    }

    @GetMapping("/meuPerfil")
    public String meuPerfil(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> {
                        model.addAttribute("usuarioLogado", usuario);
                        model.addAttribute("pessoaLogada", usuario.getPessoa());
                    });
        }
        return "meuPerfil.html";
    }
}
package com.example.OfertaMatricula.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;
import com.example.OfertaMatricula.Repository.PessoaRepository;

@Controller
public class ReservaController {
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @GetMapping("/reserva")
    public String cadastroReserva(Model model) {
        model.addAttribute("livros", livroRepository.findAll());
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        return "formularioReserva.html";
    }

    @PostMapping("/cadastraReserva")
    public String saveReserva(@RequestParam Long livroId,
            @RequestParam Long pessoaId,
            RedirectAttributes ra) {
        Livro livro = livroRepository.findById(livroId).orElse(null);
        com.example.OfertaMatricula.Model.Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);

        if (livro == null || pessoa == null) {
            ra.addFlashAttribute("mensagem", "Livro ou pessoa não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/reserva";
        }

        if (reservaRepository.existsByLivroIdAndPessoaId(livroId, pessoaId)) {
            ra.addFlashAttribute("mensagem", "Esta pessoa já reservou este livro!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/reserva";
        }

        int posicaoFila = reservaRepository.findByLivroIdOrderByDataReservaAsc(livroId).size() + 1;
        Reserva reserva = new Reserva(livro, pessoa, LocalDateTime.now(), posicaoFila, "ATIVA");
        reservaRepository.save(reserva);

        if (livro.getStatus() != StatusLivro.MANUTENCAO) {
            boolean possuiEmprestimoAtivo = !emprestimoRepository
                    .findByLivroIdAndStatus(livroId, StatusEmprestimo.ATIVO)
                    .isEmpty();
            livro.setStatus(possuiEmprestimoAtivo ? StatusLivro.RESERVADO
                    : StatusLivro.DISPONIVEL);
            livroRepository.save(livro);
        }

        ra.addFlashAttribute("mensagem", "Reserva cadastrada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaReserva";
    }

    @GetMapping("/listaReserva")
    public String listaReserva(Model model) {
        List<Reserva> listaReserva = reservaRepository.findAll();
        model.addAttribute("listaReserva", listaReserva);
        return "listaReserva.html";
    }

    @GetMapping("/editarReserva/{id}")
    public String editarReserva(@PathVariable long id, Model model) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        model.addAttribute("reserva", reserva);
        model.addAttribute("livros", livroRepository.findAll());
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        return "editarReserva.html";
    }

    @PostMapping("/atualizarReserva")
    public String atualizarReserva(@RequestParam long id,
            @RequestParam Long livroId,
            @RequestParam Long pessoaId,
            RedirectAttributes ra) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        Livro livro = livroRepository.findById(livroId).orElse(null);
        com.example.OfertaMatricula.Model.Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);

        if (reserva == null || livro == null || pessoa == null) {
            ra.addFlashAttribute("mensagem", "Reserva, livro ou pessoa não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaReserva";
        }

        reserva.setLivro(livro);
        reserva.setPessoa(pessoa);
        reserva.setPosicaoFila(reservaRepository.findByLivroIdOrderByDataReservaAsc(livroId).size());
        reservaRepository.save(reserva);

        if (livro.getStatus() != StatusLivro.MANUTENCAO) {
            boolean possuiEmprestimoAtivo = !emprestimoRepository
                    .findByLivroIdAndStatus(livroId, StatusEmprestimo.ATIVO)
                    .isEmpty();
            livro.setStatus(possuiEmprestimoAtivo ? StatusLivro.RESERVADO
                    : StatusLivro.DISPONIVEL);
            livroRepository.save(livro);
        }

        ra.addFlashAttribute("mensagem", "Reserva atualizada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaReserva";
    }

    @GetMapping("/excluirReserva/{id}")
    public String excluirReserva(@PathVariable long id, RedirectAttributes ra) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            ra.addFlashAttribute("mensagem", "Reserva não encontrada.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaReserva";
        }

        reservaRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Reserva removida com sucesso!");

        return "redirect:/listaReserva";
    }

    @GetMapping("/excluirMinhaReserva/{id}")
    public String excluirMinhaReserva(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario == null || usuario.getPessoa() == null) {
            return "redirect:/login";
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null || reserva.getPessoa() == null
                || !reserva.getPessoa().getId().equals(usuario.getPessoa().getId())) {
            ra.addFlashAttribute("mensagem", "Você só pode remover reservas que fez.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario.getPapel() == Papel.PROFESSOR
                    ? "redirect:/meuPainelProfessor"
                    : "redirect:/meuPainelAluno";
        }

        var livro = reserva.getLivro();
        reservaRepository.deleteById(id);
        atualizarStatusLivroAposCancelarReserva(livro);
        ra.addFlashAttribute("mensagem", "Reserva removida com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return usuario.getPapel() == Papel.PROFESSOR
                ? "redirect:/meuPainelProfessor"
                : "redirect:/meuPainelAluno";
    }

    private void atualizarStatusLivroAposCancelarReserva(Livro livro) {
        if (livro == null || livro.getStatus() == StatusLivro.MANUTENCAO) {
            return;
        }

        boolean possuiReserva = !reservaRepository.findByLivroIdOrderByDataReservaAsc(livro.getId()).isEmpty();
        if (possuiReserva) {
            livro.setStatus(StatusLivro.RESERVADO);
        } else {
            boolean possuiEmprestimoAtivo = emprestimoRepository
                    .findByLivroIdAndStatus(livro.getId(), StatusEmprestimo.ATIVO)
                    .stream()
                    .anyMatch(emprestimo -> emprestimo.getStatus() == StatusEmprestimo.ATIVO);
            livro.setStatus(possuiEmprestimoAtivo ? StatusLivro.EMPRESTADO : StatusLivro.DISPONIVEL);
        }

        livroRepository.save(livro);
    }
}

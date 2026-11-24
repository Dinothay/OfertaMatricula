package com.example.OfertaMatricula.Controller;

import java.security.Principal;
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

import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;
import com.example.OfertaMatricula.Repository.PessoaRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;

@Controller
public class EmprestimoController {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @GetMapping("/emprestimo")
    public String cadastroEmprestimo(Model model) {
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "formularioEmprestimo.html";
    }

    @PostMapping("/cadastraEmprestimo")
    public String saveEmprestimo(@RequestParam Long idPessoa, @RequestParam Long idLivro,
            @RequestParam LocalDate dataRetirada, @RequestParam(required = false) LocalDate dataPrevista,
            @RequestParam(required = false) LocalDate dataDevolucao, @RequestParam StatusEmprestimo status,
            @RequestParam int renovacoes, RedirectAttributes ra) {
        Pessoa pessoa = null;
        if (pessoaRepository != null) {
            pessoa = pessoaRepository.findById(idPessoa).orElse(null);
        }
        if (pessoa == null && alunoRepository != null) {
            pessoa = alunoRepository.findById(idPessoa).orElse(null);
        }
        if (pessoa == null) {
            ra.addFlashAttribute("mensagem", "Pessoa não encontrada!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaEmprestimo";
        }
        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        boolean duplicado = status == StatusEmprestimo.ATIVO
                && emprestimoRepository.existsByPessoaIdAndLivroIdAndStatus(pessoa.getId(), livro.getId(), StatusEmprestimo.ATIVO);
        if (pessoa instanceof Aluno && status == StatusEmprestimo.ATIVO) {
            duplicado = duplicado || emprestimoRepository.existsByAlunoIdAndLivroIdAndStatus(
                    pessoa.getId(), livro.getId(), StatusEmprestimo.ATIVO);
        }
        if (duplicado) {
            ra.addFlashAttribute("mensagem", "Esta pessoa já está com este livro em empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaEmprestimo";
        }

        LocalDate devolucaoPrevista = calcularDataPrevista(dataRetirada, dataPrevista);
        emprestimoRepository.save(new Emprestimo(pessoa, livro, dataRetirada, devolucaoPrevista, dataDevolucao, status, renovacoes));
        if (status == StatusEmprestimo.ATIVO) {
            livro.setStatus(com.example.OfertaMatricula.Model.StatusLivro.EMPRESTADO);
            livroRepository.save(livro);
        }
        ra.addFlashAttribute("mensagem", "Empréstimo cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    @GetMapping("/listaEmprestimo")
    public String listaEmprestimo(Model model) {
        List<Emprestimo> listaEmprestimo = emprestimoRepository.findAll();
        model.addAttribute("listaEmprestimo", listaEmprestimo);
        return "listaEmprestimo.html";
    }

    @GetMapping("/editarEmprestimo/{id}")
    public String editarEmprestimo(@PathVariable long id, Model model) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        model.addAttribute("emprestimo", emprestimo);
        model.addAttribute("alunos", alunoRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
        model.addAttribute("livros", livroRepository.findAll());
        return "editarEmprestimo.html";
    }

    @PostMapping("/atualizarEmprestimo")
    public String atualizarEmprestimo(@RequestParam long id, @RequestParam Long idPessoa, @RequestParam Long idLivro,
            @RequestParam LocalDate dataRetirada, @RequestParam(required = false) LocalDate dataPrevista,
            @RequestParam(required = false) LocalDate dataDevolucao, @RequestParam StatusEmprestimo status,
            @RequestParam int renovacoes, RedirectAttributes ra) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        if (emprestimo == null) {
            return "redirect:/listaEmprestimo";
        }

        Pessoa pessoa = pessoaRepository.findById(idPessoa).orElseThrow();
        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        boolean existeDuplicado = status == StatusEmprestimo.ATIVO
                && emprestimoRepository.existsByPessoaIdAndLivroIdAndStatusAndIdNot(
                        pessoa.getId(), livro.getId(), StatusEmprestimo.ATIVO, id);
        if (existeDuplicado) {
            ra.addFlashAttribute("mensagem", "Esta pessoa já está com este livro em empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaEmprestimo";
        }

        LocalDate devolucaoPrevista = calcularDataPrevista(dataRetirada, dataPrevista);

        emprestimo.setPessoa(pessoa);
        emprestimo.setLivro(livro);
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevista(devolucaoPrevista);
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatus(status);
        emprestimo.setRenovacoes(renovacoes);
        emprestimoRepository.save(emprestimo);
        ra.addFlashAttribute("mensagem", "Empréstimo atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    @GetMapping("/excluirEmprestimo/{id}")
    public String excluirEmprestimo(@PathVariable long id, RedirectAttributes ra) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        if (emprestimo == null) {
            ra.addFlashAttribute("mensagem", "Empréstimo não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaEmprestimo";
        }

        Livro livro = emprestimo.getLivro();
        LocalDate hoje = LocalDate.now();
        if (emprestimo.getDataPrevista() != null && hoje.isAfter(emprestimo.getDataPrevista())) {
            emprestimo.setStatus(StatusEmprestimo.ATRASADO);
            LocalDate bloqueioAtual = emprestimo.getPessoa().getBloqueioEmprestimo();
            LocalDate novoBloqueio = hoje.plusDays(14);
            if (bloqueioAtual == null || novoBloqueio.isAfter(bloqueioAtual)) {
                emprestimo.getPessoa().setBloqueioEmprestimo(novoBloqueio);
                pessoaRepository.save(emprestimo.getPessoa());
            }
        } else {
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        }
        emprestimo.setDataDevolucao(hoje);
        emprestimoRepository.save(emprestimo);

        Reserva primeiraReserva = reservaRepository.findFirstByLivroIdOrderByDataReservaAsc(livro.getId()).orElse(null);
        if (primeiraReserva != null) {
            reservaRepository.delete(primeiraReserva);
            emprestimoRepository.save(new Emprestimo(primeiraReserva.getPessoa(), livro, LocalDate.now(),
                    LocalDate.now().plusDays(14), null, StatusEmprestimo.ATIVO, 0));
            livro.setStatus(com.example.OfertaMatricula.Model.StatusLivro.EMPRESTADO);
            livroRepository.save(livro);
            ra.addFlashAttribute("mensagem", "Livro devolvido. O próximo usuário da fila recebeu o empréstimo!");
        } else {
            livro.setStatus(com.example.OfertaMatricula.Model.StatusLivro.DISPONIVEL);
            livroRepository.save(livro);
            ra.addFlashAttribute("mensagem", "Livro devolvido com sucesso!");
        }
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    @PostMapping("/renovarMeuEmprestimo/{id}")
    public String renovarMeuEmprestimo(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;
        if (usuario == null || usuario.getPessoa() == null) {
            return "redirect:/login";
        }

        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        if (emprestimo == null || emprestimo.getPessoa() == null
                || !emprestimo.getPessoa().getId().equals(usuario.getPessoa().getId())
                || emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            ra.addFlashAttribute("mensagem", "Este empréstimo não pode ser renovado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario.getPapel() == com.example.OfertaMatricula.Model.Papel.PROFESSOR
                    ? "redirect:/meuPainelProfessor" : "redirect:/meuPainelAluno";
        }

        if (!reservaRepository.findByLivroIdOrderByDataReservaAsc(emprestimo.getLivro().getId()).isEmpty()) {
            ra.addFlashAttribute("mensagem", "Não é possível renovar: existem reservas para este livro.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario.getPapel() == com.example.OfertaMatricula.Model.Papel.PROFESSOR
                    ? "redirect:/meuPainelProfessor" : "redirect:/meuPainelAluno";
        }

        if (emprestimo.getRenovacoes() >= 3) {
            ra.addFlashAttribute("mensagem", "Este empréstimo já alcançou o limite de 3 renovações.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario.getPapel() == com.example.OfertaMatricula.Model.Papel.PROFESSOR
                    ? "redirect:/meuPainelProfessor" : "redirect:/meuPainelAluno";
        }

        emprestimo.setDataPrevista(LocalDate.now().plusDays(7));
        emprestimo.setRenovacoes(emprestimo.getRenovacoes() + 1);
        emprestimoRepository.save(emprestimo);

        ra.addFlashAttribute("mensagem", "Prazo do empréstimo renovado em 7 dias!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return usuario.getPapel() == com.example.OfertaMatricula.Model.Papel.PROFESSOR
                ? "redirect:/meuPainelProfessor" : "redirect:/meuPainelAluno";
    }

    @GetMapping("/removerEmprestimo/{id}")
    public String removerEmprestimo(@PathVariable long id, RedirectAttributes ra) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);
        if (emprestimo == null) {
            ra.addFlashAttribute("mensagem", "Empréstimo não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaEmprestimo";
        }

        emprestimoRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Empréstimo excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaEmprestimo";
    }

    private LocalDate calcularDataPrevista(LocalDate dataRetirada, LocalDate dataPrevista) {
        if (dataPrevista != null && !dataPrevista.isBefore(dataRetirada)) {
            return dataPrevista;
        }
        return dataRetirada.plusDays(14);
    }
}

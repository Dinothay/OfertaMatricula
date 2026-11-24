package com.example.OfertaMatricula.Controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Repository.DisciplinaLivroRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;
import com.example.OfertaMatricula.Repository.ProvarRepository;
import com.example.OfertaMatricula.Repository.PessoaRepository;
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

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ProvarRepository provarRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @GetMapping("/livro")
    public String cadastroLivro(Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem cadastrar livros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        return "formularioLivro.html";
    }

    @PostMapping("/cadastraLivro")
    public String saveLivro(@RequestParam String titulo, @RequestParam String autor, @RequestParam String isbn,
            @RequestParam String edicao, @RequestParam StatusLivro status,
            @RequestParam("capalmagem") MultipartFile capalmagem, Principal principal, RedirectAttributes ra)
            throws IOException {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem cadastrar livros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        String caminho = livroService.salvarLogo(capalmagem);
        livroRepository.save(new Livro(titulo, autor, isbn, edicao, caminho, status));
        ra.addFlashAttribute("mensagem", "Livro cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @GetMapping("/listaLivro")
    public String listaLivro(Model model, Principal principal) {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("listaLivro", livroRepository.findAll());
        return "listaLivro.html";
    }

    @PostMapping("/reservarLivro/{id}")
    public String reservarLivro(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario == null
                || usuario.getPessoa() == null
                || (usuario.getPapel() != Papel.ALUNO
                        && usuario.getPapel() != Papel.PROFESSOR)) {
            ra.addFlashAttribute("mensagem", "Acesso inválido para reserva.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario != null && usuario.getPapel() == Papel.PROFESSOR ? "redirect:/indexProfessor"
                    : "redirect:/indexAluno";
        }

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        var pessoa = usuario.getPessoa();
        if (emprestimoRepository.existsByAlunoIdAndLivroIdAndStatus(pessoa.getId(), id, StatusEmprestimo.ATIVO)) {
            ra.addFlashAttribute("mensagem", "Você já está com este livro em sua posse e não pode reservar novamente.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        boolean jaReservou = reservaRepository.existsByLivroIdAndAlunoId(id, pessoa.getId());
        if (jaReservou) {
            ra.addFlashAttribute("mensagem", "Você já reservou este livro.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        if (livro.getStatus() == StatusLivro.MANUTENCAO) {
            ra.addFlashAttribute("mensagem", "Este livro está em manutenção e não pode ser reservado ou emprestado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        List<Reserva> reservas = reservaRepository.findByLivroIdOrderByDataReservaAsc(id);
        int posicao = reservas.size() + 1;
        reservaRepository.save(new Reserva(livro, pessoa, java.time.LocalDateTime.now(), posicao, "PENDENTE"));
        if (livro.getStatus() == StatusLivro.EMPRESTADO) {
            livro.setStatus(StatusLivro.RESERVADO);
            livroRepository.save(livro);
        }

        ra.addFlashAttribute("mensagem", "Livro reservado com sucesso! Você ficou em posição " + posicao + " da fila.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @PostMapping("/pegarLivroEmprestimo/{id}")
    public String pegarLivroEmprestimo(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario == null
                || usuario.getPessoa() == null
                || (usuario.getPapel() != Papel.ALUNO
                        && usuario.getPapel() != Papel.PROFESSOR)) {
            ra.addFlashAttribute("mensagem", "Acesso inválido para empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario != null && usuario.getPapel() == Papel.PROFESSOR ? "redirect:/indexProfessor"
                    : "redirect:/indexAluno";
        }

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        var pessoa = usuario.getPessoa();

        if (pessoa.getBloqueioEmprestimo() != null
                && pessoa.getBloqueioEmprestimo().isAfter(LocalDate.now())) {
            ra.addFlashAttribute("mensagem", "Você está temporariamente bloqueado para empréstimos e reservas até "
                    + pessoa.getBloqueioEmprestimo() + ".");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        if (livro.getStatus() == StatusLivro.MANUTENCAO) {
            ra.addFlashAttribute("mensagem", "Este livro está em manutenção e não pode ser reservado ou emprestado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        if (emprestimoRepository.existsByAlunoIdAndLivroIdAndStatus(pessoa.getId(), livro.getId(),
                StatusEmprestimo.ATIVO)) {
            ra.addFlashAttribute("mensagem", "Você já está com este livro em empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        Reserva primeiraReserva = reservaRepository.findFirstByLivroIdOrderByDataReservaAsc(id).orElse(null);

        if (livro.getStatus() == StatusLivro.DISPONIVEL && primeiraReserva != null
                && !primeiraReserva.getPessoa().getId().equals(pessoa.getId())) {
            ra.addFlashAttribute("mensagem", "Este livro está reservado para outra pessoa na fila de espera.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        if (livro.getStatus() == StatusLivro.DISPONIVEL && primeiraReserva != null
                && primeiraReserva.getPessoa().getId().equals(pessoa.getId())) {
            reservaRepository.delete(primeiraReserva);
        }

        if (livro.getStatus() != StatusLivro.DISPONIVEL) {
            boolean jaReservou = reservaRepository.existsByLivroIdAndAlunoId(id, pessoa.getId());
            if (jaReservou) {
                ra.addFlashAttribute("mensagem", "Você já está na fila de espera para este livro.");
                ra.addFlashAttribute("mensagemTipo", "erro");
                return "redirect:/listaLivro";
            }

            List<Reserva> reservas = reservaRepository.findByLivroIdOrderByDataReservaAsc(id);
            int posicao = reservas.size() + 1;
            reservaRepository.save(new Reserva(livro, pessoa, java.time.LocalDateTime.now(), posicao, "PENDENTE"));
            livro.setStatus(StatusLivro.RESERVADO);
            livroRepository.save(livro);

            ra.addFlashAttribute("mensagem",
                    "Livro indisponível. Você foi colocado na fila de reserva e receberá a prioridade quando estiver disponível.");
            ra.addFlashAttribute("mensagemTipo", "sucesso");
            return "redirect:/listaLivro";
        }

        long emprestimosAtivos = emprestimoRepository.countByAlunoIdAndStatus(pessoa.getId(), StatusEmprestimo.ATIVO);
        if (emprestimosAtivos >= 3) {
            ra.addFlashAttribute("mensagem", "Você já atingiu o limite de 3 livros emprestados.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        LocalDate hoje = LocalDate.now();
        emprestimoRepository
                .save(new Emprestimo(pessoa, livro, hoje, hoje.plusDays(14), null, StatusEmprestimo.ATIVO, 0));
        livro.setStatus(StatusLivro.EMPRESTADO);
        livroRepository.save(livro);

        ra.addFlashAttribute("mensagem", "Livro emprestado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @GetMapping("/editarLivro/{id}")
    public String editarLivro(@PathVariable long id, Model model, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem editar livros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        Livro livro = livroRepository.findById(id).orElse(null);
        model.addAttribute("livro", livro);
        return "editarLivro.html";
    }

    @PostMapping("/atualizarLivro")
    public String atualizarLivro(@RequestParam long id, @RequestParam String titulo, @RequestParam String autor,
            @RequestParam String isbn, @RequestParam String edicao, @RequestParam StatusLivro status,
            @RequestParam(value = "capalmagem", required = false) MultipartFile capalmagem,
            Principal principal, RedirectAttributes ra) throws IOException {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem editar livros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setEdicao(edicao);
        livro.setStatus(status);
        if (capalmagem != null && !capalmagem.isEmpty()) {
            livro.setCapalmagem(livroService.salvarLogo(capalmagem));
        }
        livroRepository.save(livro);
        ra.addFlashAttribute("mensagem", "Livro atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @GetMapping("/excluirLivro/{id}")
    public String excluirLivro(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem excluir livros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }
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

        boolean associadoAProva = provarRepository.findAll().stream()
                .anyMatch(prova -> prova.getLivros() != null
                        && prova.getLivros().stream().anyMatch(livro -> livro.getId().equals(id)));
        if (associadoAProva) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: livro está associado a uma prova.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        boolean associadoAReserva = reservaRepository.findAll().stream()
                .anyMatch(reserva -> reserva.getLivro() != null
                        && reserva.getLivro().getId().equals(id));
        if (associadoAReserva) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: livro está associado a uma reserva.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        try {
            livroRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: o livro está associado a outros registros.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        ra.addFlashAttribute("mensagem", "Livro excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }

    @PostMapping("/devolverLivro/{id}")
    public String devolverLivro(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null ? usuarioRepository.findByUsername(principal.getName()).orElse(null) : null;
        if (usuario == null || (usuario.getPapel() != Papel.ADMIN && usuario.getPapel() != Papel.BIBLIOTECARIO)) {
            ra.addFlashAttribute("mensagem", "Apenas administradores e bibliotecários podem registrar devoluções.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            ra.addFlashAttribute("mensagem", "Livro não encontrado.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaLivro";
        }

        LocalDate hoje = LocalDate.now();
        emprestimoRepository.findByLivroIdAndStatus(id, StatusEmprestimo.ATIVO).forEach(emprestimo -> {
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
        });

        Reserva primeiraReserva = reservaRepository.findFirstByLivroIdOrderByDataReservaAsc(id).orElse(null);
        if (primeiraReserva != null) {
            reservaRepository.delete(primeiraReserva);
            emprestimoRepository.save(new Emprestimo(primeiraReserva.getPessoa(), livro, LocalDate.now(),
                    LocalDate.now().plusDays(14), null, StatusEmprestimo.ATIVO, 0));
            livro.setStatus(StatusLivro.EMPRESTADO);
            livroRepository.save(livro);
            ra.addFlashAttribute("mensagem", "Devolução registrada. O próximo usuário da fila recebeu o empréstimo.");
        } else {
            livro.setStatus(StatusLivro.DISPONIVEL);
            livroRepository.save(livro);
            ra.addFlashAttribute("mensagem", "Devolução registrada. O livro está disponível.");
        }
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaLivro";
    }
}
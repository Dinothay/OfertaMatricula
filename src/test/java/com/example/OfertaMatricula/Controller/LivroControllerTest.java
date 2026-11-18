package com.example.OfertaMatricula.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.OfertaMatricula.Controller.EmprestimoController;
import com.example.OfertaMatricula.Controller.MatriculaController;
import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Curso;
import com.example.OfertaMatricula.Model.Disciplina;
import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.OfertaDisciplina;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Model.Prova;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;
import com.example.OfertaMatricula.Repository.ProvarRepository;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.CursoRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

class LivroControllerTest {

    @Test
    void deveBloquearQuartoEmprestimoParaMesmoAluno() {
        LivroRepository livroRepository = mock(LivroRepository.class);
        EmprestimoRepository emprestimoRepository = mock(EmprestimoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        ReservaRepository reservaRepository = mock(ReservaRepository.class);

        LivroController controller = new LivroController();
        controller.setLivroRepository(livroRepository);
        controller.setUsuarioRepository(usuarioRepository);
        controller.setEmprestimoRepository(emprestimoRepository);
        controller.setReservaRepository(reservaRepository);

        var book = new Livro();
        book.setId(10L);
        book.setStatus(StatusLivro.DISPONIVEL);

        Aluno aluno = new Aluno();
        aluno.setId(7L);

        Usuario usuario = new Usuario();
        usuario.setUsername("aluno1");
        usuario.setPapel(Papel.ALUNO);
        usuario.setPessoa(aluno);

        Principal principal = () -> "aluno1";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        when(usuarioRepository.findByUsername("aluno1")).thenReturn(java.util.Optional.of(usuario));
        when(livroRepository.findById(10L)).thenReturn(java.util.Optional.of(book));
        when(emprestimoRepository.countByAlunoIdAndStatus(aluno.getId(), com.example.OfertaMatricula.Model.StatusEmprestimo.ATIVO)).thenReturn(3L);
        when(reservaRepository.findFirstByLivroIdOrderByDataReservaAsc(10L)).thenReturn(java.util.Optional.empty());

        String view = controller.pegarLivroEmprestimo(10L, principal, redirectAttributes);

        assertEquals("redirect:/listaLivro", view);
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveReservarLivroQuandoEleJaEstiverEmprestado() {
        LivroRepository livroRepository = mock(LivroRepository.class);
        EmprestimoRepository emprestimoRepository = mock(EmprestimoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        ReservaRepository reservaRepository = mock(ReservaRepository.class);

        LivroController controller = new LivroController();
        controller.setLivroRepository(livroRepository);
        controller.setUsuarioRepository(usuarioRepository);
        controller.setEmprestimoRepository(emprestimoRepository);
        controller.setReservaRepository(reservaRepository);

        Livro livro = new Livro();
        livro.setId(20L);
        livro.setStatus(StatusLivro.EMPRESTADO);

        Aluno aluno = new Aluno();
        aluno.setId(11L);

        Usuario usuario = new Usuario();
        usuario.setUsername("aluno2");
        usuario.setPapel(Papel.ALUNO);
        usuario.setPessoa(aluno);

        Principal principal = () -> "aluno2";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        when(usuarioRepository.findByUsername("aluno2")).thenReturn(java.util.Optional.of(usuario));
        when(livroRepository.findById(20L)).thenReturn(java.util.Optional.of(livro));
        when(reservaRepository.findFirstByLivroIdOrderByDataReservaAsc(20L)).thenReturn(java.util.Optional.empty());
        when(reservaRepository.findByLivroIdOrderByDataReservaAsc(20L)).thenReturn(java.util.Collections.emptyList());
        when(reservaRepository.existsByLivroIdAndAlunoId(20L, 11L)).thenReturn(false);

        String view = controller.pegarLivroEmprestimo(20L, principal, redirectAttributes);

        assertEquals("redirect:/listaLivro", view);
        verify(reservaRepository).save(any(Reserva.class));
        verify(livroRepository).save(livro);
    }

    @Test
    void deveBloquearMatriculaDuplicadaParaMesmoAlunoNaMesmaOferta() throws Exception {
        MatriculaController controller = new MatriculaController();
        MatriculaRepository matriculaRepository = mock(MatriculaRepository.class);
        AlunoRepository alunoRepository = mock(AlunoRepository.class);
        CursoRepository cursoRepository = mock(CursoRepository.class);
        OfertaDisciplinaRepository ofertaDisciplinaRepository = mock(OfertaDisciplinaRepository.class);

        setField(controller, "matriculaRepository", matriculaRepository);
        setField(controller, "alunoRepository", alunoRepository);
        setField(controller, "cursoRepository", cursoRepository);
        setField(controller, "ofertaDisciplinaRepository", ofertaDisciplinaRepository);

        Aluno aluno = new Aluno();
        aluno.setId(1L);
        Curso curso = new Curso();
        curso.setId(2L);
        OfertaDisciplina ofertaDisciplina = new OfertaDisciplina();
        ofertaDisciplina.setId(3L);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(ofertaDisciplinaRepository.findById(3L)).thenReturn(Optional.of(ofertaDisciplina));
        when(matriculaRepository.existsByAlunoIdAndOfertaDisciplinaId(1L, 3L)).thenReturn(true);

        String view = controller.saveMatricula(3L, 1L, 2L, LocalDate.of(2026, 11, 13), new RedirectAttributesModelMap());

        assertEquals("redirect:/listaMatricula", view);
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void deveBloquearEmprestimoDuplicadoParaMesmoAlunoELivro() throws Exception {
        EmprestimoController controller = new EmprestimoController();
        EmprestimoRepository emprestimoRepository = mock(EmprestimoRepository.class);
        AlunoRepository alunoRepository = mock(AlunoRepository.class);
        LivroRepository livroRepository = mock(LivroRepository.class);

        setField(controller, "emprestimoRepository", emprestimoRepository);
        setField(controller, "alunoRepository", alunoRepository);
        setField(controller, "livroRepository", livroRepository);

        Aluno aluno = new Aluno();
        aluno.setId(10L);
        Livro livro = new Livro();
        livro.setId(20L);

        when(alunoRepository.findById(10L)).thenReturn(Optional.of(aluno));
        when(livroRepository.findById(20L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByAlunoIdAndLivroIdAndStatus(10L, 20L, StatusEmprestimo.ATIVO)).thenReturn(true);

        String view = controller.saveEmprestimo(
            10L,
            20L,
            LocalDate.of(2026, 11, 13),
            LocalDate.of(2026, 11, 27),
            null,
            StatusEmprestimo.ATIVO,
            0,
            new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/listaEmprestimo", view);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void deveBloquearReservaQuandoAlunoJaPossuiLivro() {
        LivroRepository livroRepository = mock(LivroRepository.class);
        EmprestimoRepository emprestimoRepository = mock(EmprestimoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        ReservaRepository reservaRepository = mock(ReservaRepository.class);

        LivroController controller = new LivroController();
        controller.setLivroRepository(livroRepository);
        controller.setUsuarioRepository(usuarioRepository);
        controller.setEmprestimoRepository(emprestimoRepository);
        controller.setReservaRepository(reservaRepository);

        Livro livro = new Livro();
        livro.setId(30L);
        livro.setStatus(StatusLivro.DISPONIVEL);

        Aluno aluno = new Aluno();
        aluno.setId(12L);

        Usuario usuario = new Usuario();
        usuario.setUsername("aluno3");
        usuario.setPapel(Papel.ALUNO);
        usuario.setPessoa(aluno);

        Principal principal = () -> "aluno3";
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        when(usuarioRepository.findByUsername("aluno3")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(30L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByAlunoIdAndLivroIdAndStatus(12L, 30L, StatusEmprestimo.ATIVO)).thenReturn(true);

        String view = controller.reservarLivro(30L, principal, redirectAttributes);

        assertEquals("redirect:/listaLivro", view);
        verify(reservaRepository, never()).save(any(Reserva.class));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveBloquearEdicaoDeProvaDeOutroProfessor() throws Exception {
        ProvaController controller = new ProvaController();
        DisciplinaRepository disciplinaRepository = mock(DisciplinaRepository.class);
        ProvarRepository provarRepository = mock(ProvarRepository.class);
        LivroRepository livroRepository = mock(LivroRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

        setField(controller, "disciplinaRepository", disciplinaRepository);
        setField(controller, "provarRepository", provarRepository);
        setField(controller, "livroRepository", livroRepository);
        setField(controller, "usuarioRepository", usuarioRepository);

        Professor professorLogado = new Professor();
        professorLogado.setId(7L);
        Professor professorDono = new Professor();
        professorDono.setId(9L);

        Usuario usuario = new Usuario();
        usuario.setUsername("prof1");
        usuario.setPapel(Papel.PROFESSOR);
        usuario.setPessoa(professorLogado);

        Prova prova = new Prova();
        prova.setId(15L);
        prova.setProfessor(professorDono);
        prova.setDisciplina(new Disciplina());
        prova.setConteudo("Conteúdo");
        prova.setPeso(10.0);

        when(usuarioRepository.findByUsername("prof1")).thenReturn(Optional.of(usuario));
        when(provarRepository.findById(15L)).thenReturn(Optional.of(prova));

        Principal principal = () -> "prof1";
        var model = new org.springframework.ui.ExtendedModelMap();

        String view = controller.editarProva(15L, model, principal, new RedirectAttributesModelMap());

        assertEquals("redirect:/listaProva", view);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

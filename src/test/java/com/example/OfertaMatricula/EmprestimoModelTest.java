package com.example.OfertaMatricula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;

class EmprestimoModelTest {

    @Test
    void deveRelacionarAlunoELivroAoEmprestimo() {
        Aluno aluno = new Aluno();
        Livro livro = new Livro();
        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataRetirada(LocalDate.of(2025, 1, 10));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        assertNotNull(emprestimo.getAluno());
        assertNotNull(emprestimo.getLivro());
        assertEquals(LocalDate.of(2025, 1, 10), emprestimo.getDataRetirada());
        assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Test
    void deveDefinirStatusDoLivro() {
        Livro livro = new Livro();

        livro.setTitulo("Clean Code");
        livro.setStatus(StatusLivro.DISPONIVEL);

        assertEquals("Clean Code", livro.getTitulo());
        assertEquals(StatusLivro.DISPONIVEL, livro.getStatus());
    }

    @Test
    void deveRelacionarUsuarioComPessoaEManterSenhaEFotoNulas() {
        Pessoa pessoa = new Pessoa("Maria", "12345678909", "Pai", "Mae", "Rua A", "maria@email.com", "11999999999");
        Usuario usuario = new Usuario();

        usuario.setUsername("maria.aluno");
        usuario.setPapel(Papel.ALUNO);
        usuario.setPessoa(pessoa);

        assertEquals("maria.aluno", usuario.getUsername());
        assertEquals(Papel.ALUNO, usuario.getPapel());
        assertEquals(pessoa, usuario.getPessoa());
        assertNull(usuario.getPassword());
        assertNull(usuario.getFotoPerfil());
    }
}

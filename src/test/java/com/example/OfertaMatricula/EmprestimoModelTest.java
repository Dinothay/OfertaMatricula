package com.example.OfertaMatricula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.Exemplar;
import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.StatusEmprestimo;
import com.example.OfertaMatricula.Model.StatusExemplar;

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
    void deveDefinirStatusDoExemplar() {
        Exemplar exemplar = new Exemplar();

        exemplar.setTombo("T-100");
        exemplar.setStatus(StatusExemplar.DISPONIVEL);

        assertEquals("T-100", exemplar.getTombo());
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
    }
}

package com.example.OfertaMatricula.Model;

import java.util.Objects;

public class DisciplinaLivroId{

    private long disciplina;
    private long livro;

    public DisciplinaLivroId() {
    }

    public DisciplinaLivroId(long disciplina, long livro) {
        this.disciplina = disciplina;
        this.livro = livro;
    }

    public long getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(long disciplina) {
        this.disciplina = disciplina;
    }

    public Long getLivro() {
        return livro;
    }

    public void setLivro(Long livro) {
        this.livro = livro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DisciplinaLivroId)) {
            return false;
        }

        DisciplinaLivroId that = (DisciplinaLivroId) o;

        return disciplina == that.disciplina
                && Objects.equals(livro, that.livro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(disciplina, livro);
    }
}
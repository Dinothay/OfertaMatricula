package com.example.OfertaMatricula.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "disciplina_livro")
@IdClass(DisciplinaLivroId.class)
public class DisciplinaLivro {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_disciplina")
    private Disciplina disciplina;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private Tipo tipo;

    public DisciplinaLivro() {
    }

    public DisciplinaLivro(Disciplina disciplina, Livro livro, Tipo tipo) {
        this.disciplina = disciplina;
        this.livro = livro;
        this.tipo = tipo;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
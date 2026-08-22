package com.example.OfertaMatricula.Model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Matricula {
    public Matricula() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ofertaDisciplina")
    private OfertaDisciplina ofertaDisciplina;

    @ManyToOne
    @JoinColumn(name = "curso")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "aluno")
    private Aluno aluno;

    @Column(name = "dataM")
    private LocalDate dataM;

    public Matricula(OfertaDisciplina ofertaDisciplina, Curso curso, Aluno aluno, LocalDate dataM) {
        this.ofertaDisciplina = ofertaDisciplina;
        this.curso = curso;
        this.aluno = aluno;
        this.dataM = dataM;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OfertaDisciplina getOfertaDisciplina() {
        return ofertaDisciplina;
    }

    public void setOfertaDisciplina(OfertaDisciplina ofertaDisciplina) {
        this.ofertaDisciplina = ofertaDisciplina;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public LocalDate getDataM() {
        return dataM;
    }

    public void setDataM(LocalDate dataM) {
        this.dataM = dataM;
    }
}

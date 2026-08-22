package com.example.OfertaMatricula.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Disciplina {
    public Disciplina(String nome, int semestre, int nAulas, Double cHoraria) {
        this.nome = nome;
        this.semestre = semestre;
        this.nAulas = nAulas;
        this.cHoraria = cHoraria;
    }

    public Disciplina() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "semestres")
    private int semestre;

    @Column(name = "nAulas")
    private int nAulas;

    @Column(name = "cHoraria")
    private Double cHoraria;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getnAulas() {
        return nAulas;
    }

    public void setnAulas(int nAulas) {
        this.nAulas = nAulas;
    }

    public Double getcHoraria() {
        return cHoraria;
    }

    public void setcHoraria(Double cHoraria) {
        this.cHoraria = cHoraria;
    }
}

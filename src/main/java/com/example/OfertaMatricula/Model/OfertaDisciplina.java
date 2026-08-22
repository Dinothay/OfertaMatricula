package com.example.OfertaMatricula.Model;

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
public class OfertaDisciplina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;


    @ManyToOne
    @JoinColumn(name = "disciplina")
    private Disciplina disciplina;

    @Column(name = "diaAula")
    private String diaAula;

    @ManyToOne
    @JoinColumn(name = "professor")
    private Professor professor;

    @Column(name = "nAulaSemana")
    private int nAulaSemana;

    

    public OfertaDisciplina(Disciplina disciplina, String diaAula, Professor professor, int nAulaSemana) {
        this.disciplina = disciplina;
        this.diaAula = diaAula;
        this.professor = professor;
        this.nAulaSemana = nAulaSemana;
    }

    public OfertaDisciplina() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public String getDiaAula() {
        return diaAula;
    }

    public void setDiaAula(String diaAula) {
        this.diaAula = diaAula;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
    public int getnAulaSemana() {
        return nAulaSemana;
    }

    public void setnAulaSemana(int nAulaSemana) {
        this.nAulaSemana = nAulaSemana;
    }
}

package com.example.OfertaMatricula.Model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "semestres")
    private int semestres;

    @Column(name = "nDisciplinas")
    private int nDisciplinas;

    public Curso() {
    }

    public Curso(String nome, int semestres, int nDisciplinas) {
        this.nome = nome;
        this.semestres = semestres;
        this.nDisciplinas = nDisciplinas;
    }

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

    public int getSemestres() {
        return semestres;
    }

    public void setSemestres(int semestres) {
        this.semestres = semestres;
    }

    public int getnDisciplinas() {
        return nDisciplinas;
    }

    public void setnDisciplinas(int nDisciplinas) {
        this.nDisciplinas = nDisciplinas;
    }

}

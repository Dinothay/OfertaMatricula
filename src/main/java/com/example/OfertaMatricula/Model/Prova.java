package com.example.OfertaMatricula.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Prova")
public class Prova {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToMany
    @JoinTable(name = "prova_livro",joinColumns = @JoinColumn(name = "prova_id"),inverseJoinColumns = @JoinColumn(name = "livro_id", nullable = true)
    )
    private List<Livro> livros = new ArrayList<>();

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "peso")
    private double peso;

    public Prova() {
    }

    public Prova(LocalDate data, String conteudo, double peso) {
        this.data = data;
        this.conteudo = conteudo;
        this.peso = peso;
    }

    public Prova(LocalDate data, String conteudo, double peso, Disciplina disciplina, List<Livro> livros) {
        this.data = data;
        this.conteudo = conteudo;
        this.peso = peso;
        this.disciplina = disciplina;
        this.livros = livros == null ? new ArrayList<>() : new ArrayList<>(livros);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros == null ? new ArrayList<>() : new ArrayList<>(livros);
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

}

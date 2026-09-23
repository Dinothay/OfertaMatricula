package com.example.OfertaMatricula.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "Livro")
public class Livro {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "titulo")
    private String titulo;

    @Column(name = "autor")
    private String autor;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "edicao")
    private String edicao;

    @Column(name = "capalmagem")
    private String capalmagem;

    public Livro() {
    }

    public Livro(String titulo, String autor, String isbn, String edicao, String capalmagem) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.edicao = edicao;
        this.capalmagem = capalmagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEdicao() {
        return edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public String getCapalmagem() {
        return capalmagem;
    }

    public void setCapalmagem(String capalmagem) {
        this.capalmagem = capalmagem;
    }
    
}

package com.example.OfertaMatricula.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Exemplar")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tombo")
    private String tombo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_exemplar")
    private StatusExemplar status;

    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;

    public Exemplar() {
    }

    public Exemplar(String tombo, StatusExemplar status, Livro livro) {
        this.tombo = tombo;
        this.status = status;
        this.livro = livro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTombo() {
        return tombo;
    }

    public void setTombo(String tombo) {
        this.tombo = tombo;
    }

    public StatusExemplar getStatus() {
        return status;
    }

    public void setStatus(StatusExemplar status) {
        this.status = status;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}
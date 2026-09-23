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
@Table(name = "Exemplar")
public class Exemplar {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long id;

    @Column(name = "tombo")
    private String tombo;

    @ManyToOne
    @JoinColumn(name = "status")
    private Emprestimo status;

    public Exemplar() {
    }

    public Exemplar(String tombo, Emprestimo status) {
        this.tombo = tombo;
        this.status = status;
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

    public Emprestimo getStatus() {
        return status;
    }

    public void setStatus(Emprestimo status) {
        this.status = status;
    }
}

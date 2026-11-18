package com.example.OfertaMatricula.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "Reserva")
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @Column(name = "dataReserva")
    private LocalDateTime dataReserva;

    @Column(name = "posicaoFila")
    private int posicaoFila;

    @Column(name = "status")
    private String status;

    public Reserva() {
    }

    public Reserva(LocalDateTime dataReserva, int posicaoFila, String status) {
        this.dataReserva = dataReserva;
        this.posicaoFila = posicaoFila;
        this.status = status;
    }

    public Reserva(Livro livro, Pessoa pessoa, LocalDateTime dataReserva, int posicaoFila, String status) {
        this.livro = livro;
        this.pessoa = pessoa;
        this.dataReserva = dataReserva;
        this.posicaoFila = posicaoFila;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Aluno getAluno() {
        return pessoa instanceof Aluno ? (Aluno) pessoa : null;
    }

    public void setAluno(Aluno aluno) {
        this.pessoa = aluno;
    }

    public void setAluno(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public int getPosicaoFila() {
        return posicaoFila;
    }

    public void setPosicaoFila(int posicaoFila) {
        this.posicaoFila = posicaoFila;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}

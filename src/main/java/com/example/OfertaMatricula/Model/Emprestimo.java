package com.example.OfertaMatricula.Model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "Emprestimo")
public class Emprestimo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "dataRetirada")
    private LocalDate dataRetirada;

    @Column (name = "dataPrevista")
    private LocalDate dataPrevista;

    @Column (name = "dataDevolucao")
    private LocalDate dataDevolucao;

    @Column (name = "status")
    private String status;
    
    @Column (name = "renovacoes")
    private int renovacoes;

    public Emprestimo() {
    }

    public Emprestimo(LocalDate dataRetirada, LocalDate dataPrevista, LocalDate dataDevolucao, String status,
            int renovacoes) {
        this.dataRetirada = dataRetirada;
        this.dataPrevista = dataPrevista;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
        this.renovacoes = renovacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRenovacoes() {
        return renovacoes;
    }

    public void setRenovacoes(int renovacoes) {
        this.renovacoes = renovacoes;
    }
    
    
}

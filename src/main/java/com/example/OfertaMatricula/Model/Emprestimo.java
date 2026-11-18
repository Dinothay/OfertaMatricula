package com.example.OfertaMatricula.Model;

import java.time.LocalDate;

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
@Table(name = "Emprestimo")
public class Emprestimo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "livro")
    private Livro livro;

    @Column (name = "dataRetirada")
    private LocalDate dataRetirada;

    @Column (name = "dataPrevista")
    private LocalDate dataPrevista;

    @Column (name = "dataDevolucao")
    private LocalDate dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column (name = "status")
    private StatusEmprestimo status;
    
    @Column (name = "renovacoes")
    private int renovacoes;

    public Emprestimo() {
    }

    public Emprestimo(Pessoa pessoa, Livro livro, LocalDate dataRetirada, LocalDate dataPrevista, LocalDate dataDevolucao,
            StatusEmprestimo status, int renovacoes) {
        this.pessoa = pessoa;
        this.livro = livro;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Aluno getAluno() {
        return pessoa instanceof Aluno ? (Aluno) pessoa : null;
    }

    public com.example.OfertaMatricula.Model.Professor getProfessor() {
        return pessoa instanceof com.example.OfertaMatricula.Model.Professor
                ? (com.example.OfertaMatricula.Model.Professor) pessoa
                : null;
    }

    public void setAluno(Aluno aluno) {
        this.pessoa = aluno;
    }

    public void setAluno(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
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

    public StatusEmprestimo getStatus() {
        return status;
    }

    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }

    public int getRenovacoes() {
        return renovacoes;
    }

    public void setRenovacoes(int renovacoes) {
        this.renovacoes = renovacoes;
    }
    
    
}

package com.example.OfertaMatricula.Model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Aluno extends Pessoa {

    @Column(name = "prontuario")
    private int prontuario;

    @Column(name = "dataInicio")
    private LocalDate dataInicio;

    public Aluno() {
    }

    public Aluno(String nome, String cpf, String email, String telefone, int prontuario,
                 String endereco, LocalDate dataInicio) {

        super(nome, cpf, "", "", endereco, email, telefone);

        this.prontuario = prontuario;
        this.dataInicio = dataInicio;
    }

    public int getProntuario() {
        return prontuario;
    }

    public void setProntuario(int prontuario) {
        this.prontuario = prontuario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
}
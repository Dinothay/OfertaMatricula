package com.example.OfertaMatricula.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Professor extends Pessoa {

    @Column(name = "area")
    private String area;

    @Column(name = "formacao")
    private String formacao;

    @Column(name = "salario")
    private Double salario;

    public Professor() {
    }

    public Professor(String nome, String cpf, String email, String telefone, String area,
                     String formacao, String endereco, Double salario) {

        super(nome, cpf, "", "", endereco, email, telefone);

        this.area = area;
        this.formacao = formacao;
        this.salario = salario;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
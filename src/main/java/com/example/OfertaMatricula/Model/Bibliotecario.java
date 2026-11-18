package com.example.OfertaMatricula.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Bibliotecario extends Pessoa {
    public Bibliotecario() {
    }

    public Bibliotecario(String nome, String cpf, String email,
            String telefone, String endereco) {
        super(nome, cpf, "", "", endereco, email, telefone);
    }
}
package com.example.OfertaMatricula.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String cpf;
    private String nomePai;
    private String nomeMae;
    private String endereco;
    private String email;
    private String telefone;

    public Pessoa(String nome, String cpf, String nomePai, String nomeMae,
            String endereco, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.nomePai = nomePai;
        this.nomeMae = nomeMae;
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
    }

    public Pessoa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // Ajuda do Chat para fazer a validação do telefone
    public static String validarTelefone(String telefone) {
        if (telefone == null) {
            return null;
        }
        telefone = telefone.replaceAll("[^0-9]", "");
        if (telefone.length() != 10 && telefone.length() != 11) {
            return null;
        }
        int ddd = Integer.parseInt(telefone.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            return null;
        }
        if (telefone.length() == 11 && telefone.charAt(2) != '9') {
            return null;
        }
        if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + "-" + telefone.substring(7, 11);
        }
        return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + "-" + telefone.substring(6, 10);
    }

    public static String validarCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return null;
        }
        if (cpf.matches("(\\d)\\1{10}")) {
            return null;
        }
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int resto = soma % 11;
        int primeiroDigito = (resto < 2) ? 0 : 11 - resto;
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return null;
        }
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        resto = soma % 11;
        int segundoDigito = (resto < 2) ? 0 : 11 - resto;
        if (segundoDigito != Character.getNumericValue(cpf.charAt(10))) {
            return null;
        }
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }
}

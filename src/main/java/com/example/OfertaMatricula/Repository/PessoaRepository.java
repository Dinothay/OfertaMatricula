package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}

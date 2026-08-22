package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula,Long>{
    
}

package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.OfertaMatricula.Model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long>{
    
}

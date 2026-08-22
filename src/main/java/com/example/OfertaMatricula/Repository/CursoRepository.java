package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.OfertaMatricula.Model.Curso;

public interface CursoRepository extends JpaRepository<Curso,Long> {

    
}

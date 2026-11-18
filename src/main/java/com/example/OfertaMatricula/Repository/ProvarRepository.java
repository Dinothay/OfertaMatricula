package com.example.OfertaMatricula.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.OfertaMatricula.Model.Prova;

public interface ProvarRepository extends JpaRepository<Prova,Long>{
   List<Prova> findByProfessorId(Long professorId);
}

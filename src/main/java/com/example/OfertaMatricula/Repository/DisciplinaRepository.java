package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina,Long> {
	boolean existsByCursoId(Long cursoId);

}

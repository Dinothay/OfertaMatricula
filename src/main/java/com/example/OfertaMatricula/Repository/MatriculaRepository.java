package com.example.OfertaMatricula.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula,Long>{
	boolean existsByAlunoId(Long alunoId);
	boolean existsByCursoId(Long cursoId);
	boolean existsByOfertaDisciplinaId(Long ofertaDisciplinaId);
	boolean existsByAlunoIdAndOfertaDisciplinaId(Long alunoId, Long ofertaDisciplinaId);
	boolean existsByAlunoIdAndOfertaDisciplinaIdAndIdNot(Long alunoId, Long ofertaDisciplinaId, Long id);
	List<Matricula> findByAlunoId(Long alunoId);
    
}

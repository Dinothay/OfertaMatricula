package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.OfertaMatricula.Model.DisciplinaLivro;
import com.example.OfertaMatricula.Model.DisciplinaLivroId;

public interface DisciplinaLivroRepository extends JpaRepository<DisciplinaLivro, DisciplinaLivroId> {
    boolean existsByLivroId(Long livroId);
    boolean existsByDisciplinaId(Long disciplinaId);
}
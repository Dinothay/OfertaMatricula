package com.example.OfertaMatricula.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.OfertaMatricula.Model.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
}

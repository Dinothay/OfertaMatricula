package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Livro;
import com.example.OfertaMatricula.Model.StatusLivro;

public interface LivroRepository extends JpaRepository<Livro,Long>{
   long countByStatus(StatusLivro status);
}

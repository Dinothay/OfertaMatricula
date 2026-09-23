package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.OfertaMatricula.Model.Livro;

public interface LivroRepository extends JpaRepository<Livro,Long>{
   
}

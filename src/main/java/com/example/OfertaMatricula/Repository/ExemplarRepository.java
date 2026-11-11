package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Exemplar;
import com.example.OfertaMatricula.Model.StatusExemplar;

public interface ExemplarRepository extends JpaRepository<Exemplar,Long>{
   long countByStatus(StatusExemplar status);
}

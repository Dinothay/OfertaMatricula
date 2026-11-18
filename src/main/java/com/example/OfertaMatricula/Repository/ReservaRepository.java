package com.example.OfertaMatricula.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.OfertaMatricula.Model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{
    List<Reserva> findByLivroIdOrderByDataReservaAsc(Long livroId);
    Optional<Reserva> findFirstByLivroIdOrderByDataReservaAsc(Long livroId);

    @Query("select case when count(r) > 0 then true else false end from Reserva r where r.livro.id = ?1 and r.pessoa.id = ?2")
    boolean existsByLivroIdAndAlunoId(Long livroId, Long pessoaId);

    @Query("select r from Reserva r where r.pessoa.id = ?1")
    List<Reserva> findByAlunoId(Long pessoaId);

    @Query("select r from Reserva r where r.pessoa.id = ?1")
    List<Reserva> findByPessoaId(Long pessoaId);

    @Query("select case when count(r) > 0 then true else false end from Reserva r where r.livro.id = ?1 and r.pessoa.id = ?2")
    boolean existsByLivroIdAndPessoaId(Long livroId, Long pessoaId);
}

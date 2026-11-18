package com.example.OfertaMatricula.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Emprestimo;
import com.example.OfertaMatricula.Model.StatusEmprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long>{
    boolean existsByPessoaId(Long pessoaId);
    boolean existsByLivroId(Long livroId);
    boolean existsByPessoaIdAndLivroIdAndStatus(Long pessoaId, Long livroId, StatusEmprestimo status);
    boolean existsByPessoaIdAndLivroIdAndStatusAndIdNot(Long pessoaId, Long livroId, StatusEmprestimo status, Long id);
    long countByPessoaIdAndStatus(Long pessoaId, StatusEmprestimo status);
    List<Emprestimo> findByPessoaId(Long pessoaId);
    List<Emprestimo> findByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

    default boolean existsByAlunoId(Long pessoaId) {
        return existsByPessoaId(pessoaId);
    }

    default boolean existsByAlunoIdAndLivroIdAndStatus(Long pessoaId, Long livroId, StatusEmprestimo status) {
        return existsByPessoaIdAndLivroIdAndStatus(pessoaId, livroId, status);
    }

    default boolean existsByAlunoIdAndLivroIdAndStatusAndIdNot(Long pessoaId, Long livroId, StatusEmprestimo status, Long id) {
        return existsByPessoaIdAndLivroIdAndStatusAndIdNot(pessoaId, livroId, status, id);
    }

    default long countByAlunoIdAndStatus(Long pessoaId, StatusEmprestimo status) {
        return countByPessoaIdAndStatus(pessoaId, status);
    }

    default List<Emprestimo> findByAlunoId(Long pessoaId) {
        return findByPessoaId(pessoaId);
    }
}

package com.example.OfertaMatricula.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OfertaMatricula.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}

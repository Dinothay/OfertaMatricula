package com.example.OfertaMatricula;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Curso;
import com.example.OfertaMatricula.Model.Disciplina;
import com.example.OfertaMatricula.Model.OfertaDisciplina;
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.CursoRepository;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@SpringBootApplication
public class OfertaMatriculaApplication {

	@Bean
	public CommandLineRunner criarDadosTeste(
			UsuarioRepository usuarioRepository,
			AlunoRepository alunoRepository,
			ProfessorRepository professorRepository,
			CursoRepository cursoRepository,
			DisciplinaRepository disciplinaRepository,
			OfertaDisciplinaRepository ofertaDisciplinaRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.count() == 0) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setPapel(Papel.ADMIN);
				admin.setFotoPerfil(null);
				usuarioRepository.save(admin);
				System.out.println("Usuário administrador padrão criado: admin / admin");
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(OfertaMatriculaApplication.class, args);
	}

}

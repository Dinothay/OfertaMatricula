package com.example.OfertaMatricula.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Model.Curso;
import com.example.OfertaMatricula.Model.Matricula;
import com.example.OfertaMatricula.Model.OfertaDisciplina;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.CursoRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;

@Controller
public class MatriculaController {
    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaDisciplinaRepository;

    @GetMapping("/matricula")
    public String cadastroMatriculas(Model model) {
        List<Aluno> alunos = alunoRepository.findAll();
        List<Curso> cursos = cursoRepository.findAll();
        List<OfertaDisciplina> ofertas = ofertaDisciplinaRepository.findAll();
        model.addAttribute("alunos", alunos);
        model.addAttribute("cursos", cursos);
        model.addAttribute("ofertas", ofertas);
        return "formularioMatricula.html";
    }

    @PostMapping("/cadastrarMatriculas")
    public String saveMatricula(@RequestParam Long idOfertaDisciplina, @RequestParam Long idAluno,
            @RequestParam Long idCurso, @RequestParam LocalDate dataM, RedirectAttributes ra) {
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Curso curso = cursoRepository.findById(idCurso).orElseThrow();
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(idOfertaDisciplina).orElseThrow();
        matriculaRepository.save(new Matricula(ofertaDisciplina, curso, aluno, dataM));
        ra.addFlashAttribute("mensagem", "Matrícula cadastrada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaMatricula";
    }

    @GetMapping("/listaMatricula")
    public String listaMatricula(Model model) {
        List<Matricula> listaMatricula = matriculaRepository.findAll();
        model.addAttribute("listaMatricula", listaMatricula);
        return "listaMatricula.html";
    }

    @GetMapping("/editarMatricula/{id}")
    public String editarMatricula(@PathVariable long id, Model model) {
        Matricula matricula = matriculaRepository.findById(id).orElse(null);
        List<Aluno> alunos = alunoRepository.findAll();
        List<Curso> cursos = cursoRepository.findAll();
        List<OfertaDisciplina> ofertas = ofertaDisciplinaRepository.findAll();
        model.addAttribute("matricula", matricula);
        model.addAttribute("alunos", alunos);
        model.addAttribute("cursos", cursos);
        model.addAttribute("ofertas", ofertas);
        return "editarMatricula.html";
    }

    @PostMapping("atualizarMatricula")
    public String atualizarMatricula(@RequestParam long id, @RequestParam Long idOfertaDisciplina,
            @RequestParam Long idAluno, @RequestParam Long idCurso, @RequestParam LocalDate dataM, RedirectAttributes ra) {
        Matricula matricula = matriculaRepository.findById(id).orElse(null);
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Curso curso = cursoRepository.findById(idCurso).orElseThrow();
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(idOfertaDisciplina).orElseThrow();
        matricula.setOfertaDisciplina(ofertaDisciplina);
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setDataM(dataM);
        matriculaRepository.save(matricula);
        ra.addFlashAttribute("mensagem", "Matrícula atualizada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaMatricula";
    }

    @GetMapping("/excluirMatricula/{id}")
    public String excluirMatricula(@PathVariable long id) {
        matriculaRepository.deleteById(id);
        return "redirect:/listaMatricula";
    }
}

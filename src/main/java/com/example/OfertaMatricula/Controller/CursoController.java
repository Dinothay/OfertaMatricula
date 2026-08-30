package com.example.OfertaMatricula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.OfertaMatricula.Model.Curso;
import com.example.OfertaMatricula.Repository.CursoRepository;

@Controller
public class CursoController {
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private com.example.OfertaMatricula.Repository.MatriculaRepository matriculaRepository;

    @GetMapping("curso")
    public String cadastroCursos(){
        return "formularioCurso";
    }
    
    @PostMapping("/cadastrarCurso")
    public String saveCursos(@RequestParam String nome, @RequestParam int semestres,@RequestParam int nDisciplinas){
        cursoRepository.save(new Curso(nome, semestres, nDisciplinas));
        return "redirect:/curso";
    }

    @GetMapping("/listaCursos")
    public String listaCursos(Model model){
        List<Curso> listaCursos = cursoRepository.findAll();
        model.addAttribute("listaCursos",listaCursos);
        return "listaCursos.html";
    }

    @GetMapping("editarCurso/{id}")
    public String editarCurso(@PathVariable long id, Model model){
        Curso curso = cursoRepository.findById(id).orElse(null);
        model.addAttribute("curso", curso);
        return "editarCurso.html";
    }

    @PostMapping("atualizarCurso")
    public String atualizarCurso(@RequestParam long id,@RequestParam String nome,@RequestParam int semestres, @RequestParam int nDisciplinas){
        Curso curso = cursoRepository.findById(id).get();
        curso.setNome(nome);
        curso.setSemestres(semestres);
        curso.setnDisciplinas(nDisciplinas);
        cursoRepository.save(curso);
        return "redirect:/listaCursos";
    }

    @GetMapping("excluirCurso/{id}")
    public String excluirCurso(@PathVariable long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        if(matriculaRepository.existsByCursoId(id)){
            ra.addFlashAttribute("mensagem", "Não é possível excluir: curso está associado a uma matrícula.");
            return "redirect:/listaCursos";
        }
        cursoRepository.deleteById(id);
        return "redirect:/listaCursos";
    }
}

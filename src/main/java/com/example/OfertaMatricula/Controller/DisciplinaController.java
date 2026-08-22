package com.example.OfertaMatricula.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.OfertaMatricula.Model.Disciplina;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;

@Controller
public class DisciplinaController {
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping("/disciplina")
    public String cadastroDisciplinas(){
        return "formularioDisciplina.html";
    }
    
    @PostMapping("/cadastrarDisciplinas")
    public String saveDisciplinas(@RequestParam String nome, @RequestParam int semestre,@RequestParam int nAulas, @RequestParam Double cHoraria){
        disciplinaRepository.save(new Disciplina(nome, nAulas, semestre,cHoraria));
        return "redirect:/disciplina";
    }

    @GetMapping("/listaDisciplinas")
    public String listaDisciplinas(Model model){
        List<Disciplina> listaDisciplinas = disciplinaRepository.findAll();
        model.addAttribute("listaDisciplinas",listaDisciplinas);
        return "listaDisciplinas.html";
    }

    @GetMapping("/editarDisciplina/{id}")
    public String editarDisciplina(@PathVariable long id, Model model){
        Disciplina disciplina = disciplinaRepository.findById(id).orElse(null);
        model.addAttribute("disciplina", disciplina);
        return "editarDisciplina.html";
    }

    @PostMapping("atualizarDisciplina")
    public String atualizarDisciplina(@RequestParam long id,@RequestParam String nome,@RequestParam int semestre, @RequestParam int nAulas,@RequestParam Double cHoraria){
        Disciplina disciplina = disciplinaRepository.findById(id).orElse(null);
        disciplina.setNome(nome);
        disciplina.setSemestre(semestre);
        disciplina.setnAulas(nAulas);
        disciplina.setcHoraria(cHoraria);
        disciplinaRepository.save(disciplina);
        return "redirect:/listaDisciplinas";
    }

    @GetMapping("excluirDisciplina/{id}")
    public String excluirDisciplina(@PathVariable long id){
        disciplinaRepository.deleteById(id);
        return "redirect:/listaDisciplinas";
    }
}

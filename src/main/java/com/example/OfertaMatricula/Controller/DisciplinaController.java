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
    @Autowired
    private com.example.OfertaMatricula.Repository.CursoRepository cursoRepository;
    @Autowired
    private com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository ofertaDisciplinaRepository;

    @GetMapping("/disciplina")
    public String cadastroDisciplinas(Model model){
        model.addAttribute("cursos", cursoRepository.findAll());
        return "formularioDisciplina.html";
    }
    
    @PostMapping("/cadastrarDisciplinas")
    public String saveDisciplinas(@RequestParam String nome, @RequestParam int semestre,@RequestParam int nAulas, @RequestParam Double cHoraria, @RequestParam(required = false) Long cursoId, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        com.example.OfertaMatricula.Model.Curso curso = null;
        if(cursoId == null){
            ra.addFlashAttribute("mensagem", "Selecione um curso válido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("semestre", semestre);
            ra.addFlashAttribute("nAulas", nAulas);
            ra.addFlashAttribute("cHoraria", cHoraria);
            ra.addFlashAttribute("cursoId", "");
            return "redirect:/disciplina";
        }
        curso = cursoRepository.findById(cursoId).orElse(null);
        if(curso == null){
            ra.addFlashAttribute("mensagem", "Curso não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("semestre", semestre);
            ra.addFlashAttribute("nAulas", nAulas);
            ra.addFlashAttribute("cHoraria", cHoraria);
            ra.addFlashAttribute("cursoId", "");
            return "redirect:/disciplina";
        }
        disciplinaRepository.save(new Disciplina(nome, semestre, nAulas, cHoraria, curso));
        ra.addFlashAttribute("mensagem", "Disciplina cadastrada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaDisciplinas";
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
        model.addAttribute("cursos", cursoRepository.findAll());
        return "editarDisciplina.html";
    }

    @PostMapping("atualizarDisciplina")
    public String atualizarDisciplina(@RequestParam long id,@RequestParam String nome,@RequestParam int semestre, @RequestParam int nAulas,@RequestParam Double cHoraria, @RequestParam(required = false) Long cursoId, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        if(cursoId == null){
            ra.addFlashAttribute("mensagem", "Selecione um curso válido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("semestre", semestre);
            ra.addFlashAttribute("nAulas", nAulas);
            ra.addFlashAttribute("cHoraria", cHoraria);
            ra.addFlashAttribute("cursoId", "");
            return "redirect:/editarDisciplina/" + id;
        }
        com.example.OfertaMatricula.Model.Curso curso = cursoRepository.findById(cursoId).orElse(null);
        if(curso == null){
            ra.addFlashAttribute("mensagem", "Curso não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("semestre", semestre);
            ra.addFlashAttribute("nAulas", nAulas);
            ra.addFlashAttribute("cHoraria", cHoraria);
            ra.addFlashAttribute("cursoId", "");
            return "redirect:/editarDisciplina/" + id;
        }
        Disciplina disciplina = disciplinaRepository.findById(id).orElse(null);
        disciplina.setNome(nome);
        disciplina.setSemestre(semestre);
        disciplina.setnAulas(nAulas);
        disciplina.setcHoraria(cHoraria);
        disciplina.setCurso(curso);
        disciplinaRepository.save(disciplina);
        ra.addFlashAttribute("mensagem", "Disciplina atualizada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaDisciplinas";
    }

    @GetMapping("excluirDisciplina/{id}")
    public String excluirDisciplina(@PathVariable long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        if(ofertaDisciplinaRepository.existsByDisciplinaId(id)){
            ra.addFlashAttribute("mensagem", "Não é possível excluir: disciplina está associada a uma oferta de disciplina.");
            return "redirect:/listaDisciplinas";
        }
        disciplinaRepository.deleteById(id);
        return "redirect:/listaDisciplinas";
    }
}

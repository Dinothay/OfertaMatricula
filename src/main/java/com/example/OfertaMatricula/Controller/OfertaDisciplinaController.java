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
import com.example.OfertaMatricula.Model.OfertaDisciplina;
import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Repository.DisciplinaRepository;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.ProfessorRepository;

@Controller
public class OfertaDisciplinaController {
    @Autowired
    private OfertaDisciplinaRepository ofertaDisciplinaRepository;

    @Autowired
    private com.example.OfertaMatricula.Repository.MatriculaRepository matriculaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @GetMapping("/ofertaDisciplina")
    public String cadastroDisciplinas(Model model) {
        List<Disciplina> disciplinas = disciplinaRepository.findAll();
        List<Professor> professores = professorRepository.findAll();
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("professores", professores);
        return "/formularioOfertaDisciplina.html";
    }

    @PostMapping("/cadastrarOfertaDisciplinas")
    public String saveOfertaDisciplinas(@RequestParam Long idDisciplina, @RequestParam String diaAula,
            @RequestParam int nAulaSemana, @RequestParam Long idProfessor, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Professor professor = professorRepository.findById(idProfessor).orElseThrow();
        Disciplina disciplina = disciplinaRepository.findById(idDisciplina).orElseThrow();
        ofertaDisciplinaRepository.save(new OfertaDisciplina(disciplina, diaAula, professor, nAulaSemana));
        ra.addFlashAttribute("mensagem", "Oferta de disciplina cadastrada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaOfertaDisciplinas";
    }

    @GetMapping("/listaOfertaDisciplinas")
    public String listaOfertaDisciplinas(Model model) {
        List<OfertaDisciplina> listaOfertaDisciplinas = ofertaDisciplinaRepository.findAll();
        model.addAttribute("listaOfertaDisciplinas", listaOfertaDisciplinas);
        return "listaOfertaDisciplinas.html";
    }

    @GetMapping("/editarOfertaDisciplina/{id}")
    public String editarDisciplina(@PathVariable long id, Model model) {
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(id).orElse(null);
        List<Disciplina> disciplinas = disciplinaRepository.findAll();
        List<Professor> professores = professorRepository.findAll();
        model.addAttribute("ofertaDisciplina", ofertaDisciplina);
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("professores", professores);
        return "editarOfertaDisciplina.html";
    }

    @PostMapping("/atualizarOfertaDisciplina")
    public String atualizarDisciplina(@RequestParam long id, @RequestParam Long idDisciplina,
            @RequestParam String diaAula, @RequestParam int nAulaSemana, @RequestParam Long idProfessor, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(id).orElse(null);
        Professor professor = professorRepository.findById(idProfessor).orElseThrow();
        Disciplina disciplina = disciplinaRepository.findById(idDisciplina).orElseThrow();
        ofertaDisciplina.setDisciplina(disciplina);
        ofertaDisciplina.setDiaAula(diaAula);
        ofertaDisciplina.setnAulaSemana(nAulaSemana);
        ofertaDisciplina.setProfessor(professor);
        ofertaDisciplinaRepository.save(ofertaDisciplina);
        ra.addFlashAttribute("mensagem", "Oferta de disciplina atualizada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaOfertaDisciplinas";

    }

    @GetMapping("/excluirOfertaDisciplina/{id}")
    public String excluirDisciplina(@PathVariable long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if(matriculaRepository.existsByOfertaDisciplinaId(id)){
            ra.addFlashAttribute("mensagem", "Não é possível excluir: oferta de disciplina está associada a uma matrícula.");
            return "redirect:/listaOfertaDisciplinas";
        }
        ofertaDisciplinaRepository.deleteById(id);
        return "redirect:/listaOfertaDisciplinas";
    }
}

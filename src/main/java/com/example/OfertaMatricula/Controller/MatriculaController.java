package com.example.OfertaMatricula.Controller;

import java.security.Principal;
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
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.CursoRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;
import com.example.OfertaMatricula.Repository.OfertaDisciplinaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public String saveMatricula(@RequestParam Long idOfertaDisciplina, @RequestParam Long idAluno, @RequestParam Long idCurso, @RequestParam LocalDate dataM, RedirectAttributes ra) {
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Curso curso = cursoRepository.findById(idCurso).orElseThrow();
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(idOfertaDisciplina).orElseThrow();

        if (matriculaRepository.existsByAlunoIdAndOfertaDisciplinaId(aluno.getId(), ofertaDisciplina.getId())) {
            ra.addFlashAttribute("mensagem", "Este aluno já está matriculado nessa oferta de disciplina.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaMatricula";
        }

        matriculaRepository.save(new Matricula(ofertaDisciplina, curso, aluno, dataM));
        ra.addFlashAttribute("mensagem", "Matrícula cadastrada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaMatricula";
    }

    @GetMapping("/ofertasParaMatricularAluno")
    public String ofertasParaMatricularAluno(Model model, Principal principal) {
        return "redirect:/listaOfertaDisciplinas";
    }

    @PostMapping("/matricularEmOferta/{idOfertaDisciplina}")
    public String matricularEmOferta(@PathVariable Long idOfertaDisciplina, Principal principal, RedirectAttributes ra) {
        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null || usuario.getPapel() != Papel.ALUNO || !(usuario.getPessoa() instanceof Aluno)) {
            ra.addFlashAttribute("mensagem", "Acesso inválido para matrícula.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/indexAluno";
        }

        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(idOfertaDisciplina).orElse(null);
        if (ofertaDisciplina == null) {
            ra.addFlashAttribute("mensagem", "Oferta de disciplina não encontrada.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaOfertaDisciplinas";
        }

        Aluno aluno = (Aluno) usuario.getPessoa();
        if (matriculaRepository.existsByAlunoIdAndOfertaDisciplinaId(aluno.getId(), ofertaDisciplina.getId())) {
            ra.addFlashAttribute("mensagem", "Você já está matriculado nessa oferta.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaOfertaDisciplinas";
        }

        Curso curso = ofertaDisciplina.getDisciplina().getCurso();
        matriculaRepository.save(new Matricula(ofertaDisciplina, curso, aluno, LocalDate.now()));

        ra.addFlashAttribute("mensagem", "Matrícula realizada com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaOfertaDisciplinas";
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

    @PostMapping("/atualizarMatricula")
    public String atualizarMatricula(@RequestParam long id, @RequestParam Long idOfertaDisciplina,@RequestParam Long idAluno, @RequestParam Long idCurso, @RequestParam LocalDate dataM, RedirectAttributes ra) {

        Matricula matricula = matriculaRepository.findById(id).orElse(null);
        if (matricula == null) {
            ra.addFlashAttribute("mensagem", "Matrícula não encontrada!");
            return "redirect:/listaMatricula";
        }

        LocalDate dataMatricula = matricula.getDataM();
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow();
        Curso curso = cursoRepository.findById(idCurso).orElseThrow();
        OfertaDisciplina ofertaDisciplina = ofertaDisciplinaRepository.findById(idOfertaDisciplina).orElseThrow();

        boolean existeDuplicado = matriculaRepository.existsByAlunoIdAndOfertaDisciplinaIdAndIdNot(aluno.getId(), ofertaDisciplina.getId(), id);
        if (existeDuplicado) {
            ra.addFlashAttribute("mensagem", "Este aluno já está matriculado nessa oferta de disciplina.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaMatricula";
        }

        matricula.setOfertaDisciplina(ofertaDisciplina);
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setDataM(dataMatricula);
        matriculaRepository.save(matricula);
        ra.addFlashAttribute("mensagem", "Matrícula atualizada com sucesso.");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaMatricula";
    }

    @GetMapping("/excluirMatricula/{id}")
    public String excluirMatricula(@PathVariable long id, RedirectAttributes ra) {
        matriculaRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Matrícula excluída com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaMatricula";
    }
}

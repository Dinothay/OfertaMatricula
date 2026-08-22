package com.example.OfertaMatricula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.OfertaMatricula.Model.Professor;
import com.example.OfertaMatricula.Repository.ProfessorRepository;

@Controller
public class ProfessorController {
    @Autowired
    private ProfessorRepository ProfessorRepository;

    @GetMapping("/professor")
    public String cadastroProfessores(){
        return "formularioProfessor.html";
    }
    
    @PostMapping("/cadastrarProfessores")
    public String saveProfessores(@RequestParam String nome, @RequestParam String cpf, @RequestParam String endereco, @RequestParam String area, @RequestParam String formacao, @RequestParam Double salario){
        ProfessorRepository.save(new Professor(nome, cpf, area,formacao, endereco, salario));
        return "redirect:/professor";
    }

    @GetMapping("/listaProfessores")
    public String listaProfessores(Model model){
        List<Professor> listaProfessores = ProfessorRepository.findAll();
        model.addAttribute("listaProfessores",listaProfessores);
        return "listaProfessores.html";
    }

    @GetMapping("/editarProfessor/{id}")
    public String editarProfessor(@PathVariable long id, Model model){
        Professor professor = ProfessorRepository.findById(id).orElse(null);
        model.addAttribute("professor", professor);
        return "editarProfessor.html";
    }

    @PostMapping("/atualizarProfessor")
    public String atualizarProfessor(@RequestParam long id,@RequestParam String nome,@RequestParam String cpf, @RequestParam Double salario, @RequestParam String endereco, @RequestParam String area, @RequestParam String formacao){
        Professor professor = ProfessorRepository.findById(id).orElse(null);
        professor.setNome(nome);
        professor.setCpf(cpf);
        professor.setSalario(salario);
        professor.setEndereco(endereco);
        professor.setArea(area);
        professor.setFormacao(formacao);
        ProfessorRepository.save(professor);
        return "redirect:/listaProfessores";
    }

    @GetMapping("/excluirProfessor/{id}")
    public String excluirProfessor(@PathVariable long id){
        ProfessorRepository.deleteById(id);
        return "redirect:/listaProfessores";
    }
}

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
import com.example.OfertaMatricula.Model.Aluno;
import com.example.OfertaMatricula.Repository.AlunoRepository;

@Controller
public class AlunoController {
    @Autowired
    private AlunoRepository alunoRepository;

    @GetMapping("/aluno")
    public String cadastroAlunos(){
        return "formularioAluno.html";
    }
    
    @PostMapping("/cadastrarAlunos")
    public String saveAlunos(@RequestParam String nome, @RequestParam String cpf,@RequestParam int prontuario, @RequestParam String endereco, @RequestParam LocalDate dataInicio){
        alunoRepository.save(new Aluno(nome, cpf, prontuario, endereco, dataInicio));
        return "redirect:/aluno";
    }

    @GetMapping("/listaAlunos")
    public String listaAlunos(Model model){
        List<Aluno> listaAlunos = alunoRepository.findAll();
        model.addAttribute("listaAlunos",listaAlunos);
        return "listaAlunos.html";
    }

    @GetMapping("/editarAluno/{id}")
    public String editarAluno(@PathVariable long id, Model model){
        Aluno aluno = alunoRepository.findById(id).get();
        model.addAttribute("aluno", aluno);
        return "editarAluno.html";
    }

    @PostMapping("/atualizarAluno")
    public String atualizarAluno(@RequestParam long id,@RequestParam String nome,@RequestParam String cpf, @RequestParam int prontuario, @RequestParam String endereco, @RequestParam LocalDate dataInicio){
        Aluno aluno = alunoRepository.findById(id).orElse(null);
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setProntuario(prontuario);
        aluno.setEndereco(endereco);
        aluno.setDataInicio(dataInicio);
        alunoRepository.save(aluno);
        return "redirect:/listaAlunos";
    }

    @GetMapping("/excluirAluno/{id}")
    public String excluirAluno(@PathVariable long id){
        alunoRepository.deleteById(id);
        return "redirect:/listaAlunos";
    }
}

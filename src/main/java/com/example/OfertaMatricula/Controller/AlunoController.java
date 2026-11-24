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
import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Pessoa;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.AlunoRepository;
import com.example.OfertaMatricula.Repository.EmprestimoRepository;
import com.example.OfertaMatricula.Repository.MatriculaRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Controller
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/aluno")
    public String cadastroAlunos() {
        return "formularioAluno.html";
    }

    @PostMapping("/cadastrarAlunos")
    public String saveAlunos(@RequestParam String username, @RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
            @RequestParam String telefone, @RequestParam(required = false) Integer prontuario, @RequestParam String endereco,
            @RequestParam(required = false) LocalDate dataInicio, RedirectAttributes ra) {
        LocalDate dataCadastro = (dataInicio != null) ? dataInicio : LocalDate.now();
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/aluno";
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/aluno";
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;

        if (username == null || username.isBlank()) {
            ra.addFlashAttribute("mensagem", "Informe o nome de usuário do aluno!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/aluno";
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            ra.addFlashAttribute("mensagem", "Esse nome de usuário já está cadastrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/aluno";
        }

        int prontuarioGerado = alunoRepository.findAll().stream().mapToInt(Aluno::getProntuario).max().orElse(-1) + 1;

        Aluno aluno = new Aluno(nome, cpf, email, telefone, prontuarioGerado, endereco, dataCadastro);
        alunoRepository.save(aluno);

        Usuario usuario = new Usuario(username, null, Papel.ALUNO, null, aluno);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("mensagem", "Aluno cadastrado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAlunos";
    }

    @GetMapping("/listaAlunos")
    public String listaAlunos(Model model, Principal principal) {
        List<Aluno> listaAlunos = alunoRepository.findAll();
        model.addAttribute("listaAlunos", listaAlunos);
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName())
                    .ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }
        return "listaAlunos.html";
    }

    @GetMapping("/editarAluno/{id}")
    public String editarAluno(@PathVariable long id, Model model, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario != null && usuario.getPapel() == Papel.ALUNO
                && (usuario.getPessoa() == null || !usuario.getPessoa().getId().equals(id))) {
            ra.addFlashAttribute("mensagem", "Você só pode editar seu próprio cadastro.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/meuPainelAluno";
        }

        Aluno aluno = alunoRepository.findById(id).orElse(null);
        model.addAttribute("aluno", aluno);
        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("prontuarioSomenteLeitura",
                usuario != null && usuario.getPapel() == Papel.ALUNO);
        return "editarAluno.html";
    }

    @PostMapping("/atualizarAluno")
    public String atualizarAluno(
            @RequestParam long id,
            @RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
            @RequestParam String telefone, @RequestParam int prontuario, @RequestParam String endereco,
            RedirectAttributes ra, Principal principal) {
        Aluno aluno = alunoRepository.findById(id).orElse(null);
        if (aluno == null) {
            ra.addFlashAttribute("mensagem", "Aluno não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }

        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario != null && usuario.getPapel() == Papel.ALUNO
                && (usuario.getPessoa() == null || !usuario.getPessoa().getId().equals(id))) {
            ra.addFlashAttribute("mensagem", "Você só pode editar seu próprio cadastro.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/meuPainelAluno";
        }

        LocalDate dataCadastro = aluno.getDataInicio();
        String cpfFormatado = Pessoa.validarCPF(cpf);
        if (cpfFormatado == null) {
            ra.addFlashAttribute("mensagem", "CPF inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", "");
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", telefone);
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/editarAluno/" + id;
        }
        String telefoneFormatado = Pessoa.validarTelefone(telefone);
        if (telefoneFormatado == null) {
            ra.addFlashAttribute("mensagem", "Telefone inválido!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            ra.addFlashAttribute("nome", nome);
            ra.addFlashAttribute("cpf", cpf);
            ra.addFlashAttribute("email", email);
            ra.addFlashAttribute("telefone", "");
            ra.addFlashAttribute("prontuario", prontuario);
            ra.addFlashAttribute("endereco", endereco);
            ra.addFlashAttribute("dataInicio", dataCadastro);
            return "redirect:/editarAluno/" + id;
        }
        cpf = cpfFormatado;
        telefone = telefoneFormatado;
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setEmail(email);
        aluno.setTelefone(telefone);
        if (usuario == null || usuario.getPapel() == Papel.ADMIN) {
            aluno.setProntuario(prontuario);
        }
        aluno.setEndereco(endereco);
        aluno.setDataInicio(dataCadastro);
        alunoRepository.save(aluno);
        ra.addFlashAttribute("mensagem", "Aluno atualizado com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return usuario != null && usuario.getPapel() == Papel.ALUNO
                ? "redirect:/meuPainelAluno"
                : "redirect:/listaAlunos";
    }

    @GetMapping("/excluirAluno/{id}")
    @Transactional
    public String excluirAluno(
            @PathVariable long id,
            Principal principal,
            RedirectAttributes ra) {
        Usuario usuarioLogado = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;
        if (usuarioLogado != null && usuarioLogado.getPessoa() != null
                && usuarioLogado.getPessoa().getId().equals(id)) {
            ra.addFlashAttribute("mensagem", "Você não pode excluir seu próprio cadastro!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }

        Aluno aluno = alunoRepository.findById(id).orElse(null);
        if (aluno == null) {
            ra.addFlashAttribute("mensagem", "Aluno não encontrado!");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }

        if (matriculaRepository.existsByAlunoId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: aluno está associado a uma matrícula.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }
        if (emprestimoRepository.existsByAlunoId(id)) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: aluno está associado a um empréstimo.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }
        if (!reservaRepository.findByPessoaId(id).isEmpty()) {
            ra.addFlashAttribute("mensagem", "Não é possível excluir: aluno está associado a uma reserva.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaAlunos";
        }

        Usuario usuario = aluno.getUsuario();
        if (usuario != null) {
            usuario.setPessoa(null);
            aluno.setUsuario(null);
            usuarioRepository.delete(usuario);
        }

        alunoRepository.delete(aluno);
        ra.addFlashAttribute("mensagem", "Aluno excluído com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");
        return "redirect:/listaAlunos";
    }
}

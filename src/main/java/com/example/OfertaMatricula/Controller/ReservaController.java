package com.example.OfertaMatricula.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OfertaMatricula.Model.Papel;
import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Model.StatusLivro;
import com.example.OfertaMatricula.Model.Usuario;
import com.example.OfertaMatricula.Repository.LivroRepository;
import com.example.OfertaMatricula.Repository.ReservaRepository;
import com.example.OfertaMatricula.Repository.UsuarioRepository;

@Controller
public class ReservaController {
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/reserva")
    public String cadastroReserva() {
        return "formularioReserva.html";
    }

    @PostMapping("/cadastraReserva")
    public String saveReserva(@RequestParam LocalDateTime dataReserva, @RequestParam int posicaoFila,
            @RequestParam String status) {
        reservaRepository.save(new Reserva(dataReserva, posicaoFila, status));
        return "redirect:/reserva";
    }

    @GetMapping("/listaReserva")
    public String listaReserva(Model model) {
        List<Reserva> listaReserva = reservaRepository.findAll();
        model.addAttribute("listaReserva", listaReserva);
        return "listaReserva.html";
    }

    @GetMapping("/editarReserva/{id}")
    public String editarReserva(@PathVariable long id, Model model) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        model.addAttribute("reserva", reserva);
        return "editarReserva.html";
    }

    @PostMapping("/atualizarReserva")
    public String atualizarReserva(@RequestParam long id, @RequestParam LocalDateTime dataReserva,
            @RequestParam int posicaoFila, @RequestParam String status) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        reserva.setDataReserva(dataReserva);
        reserva.setPosicaoFila(posicaoFila);
        reserva.setStatus(status);
        reservaRepository.save(reserva);
        return "redirect:/listaReserva";
    }

    @GetMapping("/excluirReserva/{id}")
    public String excluirReserva(@PathVariable long id, RedirectAttributes ra) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            ra.addFlashAttribute("mensagem", "Reserva não encontrada.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return "redirect:/listaReserva";
        }

        var livro = reserva.getLivro();
        reservaRepository.deleteById(id);
        if (livro != null && reservaRepository.findByLivroIdOrderByDataReservaAsc(livro.getId()).isEmpty()
                && livro.getStatus() != StatusLivro.MANUTENCAO) {
            livro.setStatus(StatusLivro.DISPONIVEL);
            livroRepository.save(livro);
        }
        ra.addFlashAttribute("mensagem", "Reserva removida com sucesso!");

        return "redirect:/listaReserva";
    }

    @GetMapping("/excluirMinhaReserva/{id}")
    public String excluirMinhaReserva(@PathVariable long id, Principal principal, RedirectAttributes ra) {
        Usuario usuario = principal != null
                ? usuarioRepository.findByUsername(principal.getName()).orElse(null)
                : null;

        if (usuario == null || usuario.getPessoa() == null) {
            return "redirect:/login";
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null || reserva.getPessoa() == null
                || !reserva.getPessoa().getId().equals(usuario.getPessoa().getId())) {
            ra.addFlashAttribute("mensagem", "Você só pode remover reservas que fez.");
            ra.addFlashAttribute("mensagemTipo", "erro");
            return usuario.getPapel() == Papel.PROFESSOR
                    ? "redirect:/meuPainelProfessor"
                    : "redirect:/meuPainelAluno";
        }

        reservaRepository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Reserva removida com sucesso!");
        ra.addFlashAttribute("mensagemTipo", "sucesso");

        return usuario.getPapel() == Papel.PROFESSOR
                ? "redirect:/meuPainelProfessor"
                : "redirect:/meuPainelAluno";
    }
}

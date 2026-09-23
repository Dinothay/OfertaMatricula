package com.example.OfertaMatricula.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.OfertaMatricula.Model.Reserva;
import com.example.OfertaMatricula.Repository.ReservaRepository;

@Controller
public class ReservaController {
    @Autowired
    private ReservaRepository reservaRepository;

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
    public String excluirReserva(@PathVariable long id) {
        reservaRepository.deleteById(id);
        return "redirect:/listaReserva";
    }
}

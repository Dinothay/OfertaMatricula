package com.example.OfertaMatricula.Model;

import jakarta.persistence.GeneratedValue;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "Reserva")
public class Reserva {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") 
    private Long id;

    @Column(name = "dataReserva")
    private LocalDateTime dataReserva;

    @Column(name = "posicaoFila")
    private int posicaoFila;

    @Column(name = "status")
    private String status;

    public Reserva() {
    }

    public Reserva(LocalDateTime dataReserva, int posicaoFila, String status) {
        this.dataReserva = dataReserva;
        this.posicaoFila = posicaoFila;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public int getPosicaoFila() {
        return posicaoFila;
    }

    public void setPosicaoFila(int posicaoFila) {
        this.posicaoFila = posicaoFila;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}

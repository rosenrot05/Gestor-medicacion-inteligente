/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

import java.time.LocalDateTime;

/**
 *
 * @author Rosa
 */
public class Tratamiento {

    private Paciente pacienteAsignado; // Lado 2 de la Bidireccionalidad
    private Medicamento medicina;
    private int frecuenciaHoras;
    private double dosis;
    private LocalDateTime proximaToma;
    private EstadoToma estadoActual;

    //Contadores para la adherencia
    private int tomasProgramadas;
    private int tomasCompletadas;

    public Tratamiento(Paciente p, Medicamento m, int frecuenciaHoras, double dosis, LocalDateTime proximaToma) {
        this.pacienteAsignado = p;
        this.medicina = m;
        this.frecuenciaHoras = frecuenciaHoras;
        this.dosis = dosis;
        this.proximaToma = proximaToma;
        this.estadoActual = EstadoToma.PENDIENTE; // Todo tratamiento inicia pendiente

        this.tomasProgramadas = 0;
        this.tomasCompletadas = 0;

        //Vinculamos automáticamente el tratamiento a la lista del paciente
        p.agregarTratamiento(this);
    }

    public Paciente getPacienteAsignado() {
        return pacienteAsignado;
    }

    public int getFrecuenciaHoras() {
        return frecuenciaHoras;
    }

    public void setFrecuenciaHoras(int frecuenciaHoras) {
        this.frecuenciaHoras = frecuenciaHoras;
    }

    public LocalDateTime getProximaToma() {
        return proximaToma;
    }

    public void setProximaToma(LocalDateTime fecha) {
        this.proximaToma = fecha;
    }

    public EstadoToma getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoToma estado) {
        this.estadoActual = estado;
    }

    public double getDosis() {
        return dosis;
    }

    public boolean verificarAlertaHorario() {
        // Retorna true si la hora actual ya pasó o es igual a la hora de la próxima toma
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(proximaToma) || ahora.isEqual(proximaToma);
    }

    public int getTomasProgramadas() {
        return tomasProgramadas;
    }

    public int getTomasCompletadas() {
        return tomasCompletadas;
    }

    public void registrarTomaEjecutada() {
        this.estadoActual = EstadoToma.TOMADO;

        this.tomasCompletadas++;
        this.tomasProgramadas++;

        // No nos importa si es Jarabe, Cápsula o Inhalador. El sistema sabrá cómo reducirlo y reduce la dosis que pusiste al crear el
        //tratamiento.
        this.medicina.reducirStock(this.dosis);

        // Reprogramamos la próxima toma sumando la frecuencia de horas
        this.proximaToma = this.proximaToma.plusHours(frecuenciaHoras);
        this.estadoActual = EstadoToma.PENDIENTE; // Vuelve a quedar pendiente para la próxima vez
    }

    public void registrarTomaOmitida() {
        this.estadoActual = EstadoToma.OMITIDO;
        this.tomasProgramadas++; // Suma una toma al historial, pero NO suma al éxito

        this.proximaToma = this.proximaToma.plusHours(frecuenciaHoras);
        this.estadoActual = EstadoToma.PENDIENTE;
    }
}

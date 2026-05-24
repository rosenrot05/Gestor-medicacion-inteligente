package com.medistation.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Tratamiento {

    private Paciente pacienteAsignado;
    private Medicamento medicina;
    private int frecuenciaHoras;
    private double dosis;
    private LocalDateTime proximaToma;
    private EstadoToma estadoActual;
    private List<String> historialTomas;

    // guarda cuando se tomo para calcular intervalo manual
    private LocalDateTime ultimaTomaRealizada;

    private int tomasProgramadas;
    private int tomasCompletadas;

    public Tratamiento(Paciente p, Medicamento m, int frecuenciaHoras, double dosis, LocalDateTime proximaToma) {
        this.pacienteAsignado = p;
        this.medicina = m;
        this.frecuenciaHoras = frecuenciaHoras;
        this.dosis = dosis;
        this.proximaToma = proximaToma;
        this.estadoActual = EstadoToma.PENDIENTE;
        this.historialTomas = new ArrayList<>();
        
        this.tomasProgramadas = 0;
        this.tomasCompletadas = 0;
        this.ultimaTomaRealizada = null;

        // activa el limite automatico si es liquido
        if (m instanceof Jarabe || m instanceof Inhalador) {
            m.configurarUmbralDinamico(this.dosis, 2);
        }
    }

    public Paciente getPacienteAsignado() { return pacienteAsignado; }
    
    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public void setFrecuenciaHoras(int frecuenciaHoras) { this.frecuenciaHoras = frecuenciaHoras; }
    
    public LocalDateTime getProximaToma() { return proximaToma; }
    public void setProximaToma(LocalDateTime fecha) { this.proximaToma = fecha; }
    
    public EstadoToma getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoToma estado) { this.estadoActual = estado; }
    
    public double getDosis() { return dosis; }
    public Medicamento getMedicina() { return medicina; }

    public void agregarRegistroHistorial(String registro) {
        this.historialTomas.add(registro);
    }

    public List<String> getHistorialTomas() {
        return this.historialTomas;
    }

    // valida si la hora programada ya llego o paso
    public boolean verificarAlertaHorario() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(proximaToma) || ahora.isEqual(proximaToma);
    }

    public int getTomasProgramadas() { return tomasProgramadas; }
    public int getTomasCompletadas() { return tomasCompletadas; }

    public void registrarTomaEjecutada() {
        this.estadoActual = EstadoToma.TOMADO;
        this.tomasCompletadas++;
        this.tomasProgramadas++;
        this.medicina.reducirStock(this.dosis);
        this.ultimaTomaRealizada = LocalDateTime.now();

        this.proximaToma = LocalDateTime.now().plusHours(frecuenciaHoras);
        this.estadoActual = EstadoToma.PENDIENTE;
    }

    // reprograma la siguiente toma calculando desde la hora que puso el usuario
    public void registrarTomaManual(LocalDateTime horaRealDeToma) {
        this.estadoActual = EstadoToma.TOMADO;
        this.tomasCompletadas++;
        this.tomasProgramadas++;
        this.medicina.reducirStock(this.dosis);
        this.ultimaTomaRealizada = horaRealDeToma;

        this.proximaToma = horaRealDeToma.plusHours(frecuenciaHoras);
        this.estadoActual = EstadoToma.PENDIENTE;
    }

    public void registrarTomaOmitida() {
        this.estadoActual = EstadoToma.OMITIDO;
        this.tomasProgramadas++;
        this.proximaToma = this.proximaToma.plusHours(frecuenciaHoras);
        this.estadoActual = EstadoToma.PENDIENTE;
    }
    
    // Método para poder extraer el medicamento del tratamiento
    public Medicamento getMedicamento() {
        return this.medicina; 
    }
}
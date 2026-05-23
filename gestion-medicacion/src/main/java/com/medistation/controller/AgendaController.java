package com.medistation.controller;

import com.medistation.model.Paciente;
import com.medistation.model.Tratamiento;
import com.medistation.strategy.ValidadorTratamiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaController {

    private final Paciente paciente;
    private final List<ValidadorTratamiento> validadoresActivos;

    public AgendaController(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("La agenda requiere obligatoriamente un paciente asignado.");
        }
        this.paciente = paciente;
        this.validadoresActivos = new ArrayList<>();
    }

    public void agregarReglaValidacion(ValidadorTratamiento validador) {
        if (validador != null && !validadoresActivos.contains(validador)) {
            this.validadoresActivos.add(validador);
        }
    }

    public boolean intentarAgendarTratamiento(Tratamiento nuevo) {
        for (ValidadorTratamiento validador : validadoresActivos) {
            if (!validador.validar(nuevo, paciente.getTratamientos())) {
                System.out.println("[RECHAZADO] Conflicto médico detectado al agendar.");
                return false; 
            }
        }

        this.paciente.agregarTratamiento(nuevo);
        System.out.println("[ÉXITO] Tratamiento validado y agendado.");
        return true;
    }

    // toma en tiempo real
    public void registrarToma(Tratamiento tratamiento) {
        LocalDateTime ahora = LocalDateTime.now();
        tratamiento.agregarRegistroHistorial(
            "Toma registrada el: " + ahora.toLocalDate() + " a las " + ahora.toLocalTime()
        );
        tratamiento.registrarTomaEjecutada();
        this.paciente.verificarCumplimiento();
    }

    // toma manual con hora especifica (atrasada)
    public void registrarToma(Tratamiento tratamiento, LocalDateTime horaRealDeToma) {
        tratamiento.agregarRegistroHistorial(
            "Toma MANUAL: tomada a las " + horaRealDeToma.toLocalTime()
            + " del " + horaRealDeToma.toLocalDate()
        );
        tratamiento.registrarTomaManual(horaRealDeToma);
        this.paciente.verificarCumplimiento();
    }

    public void omitirToma(Tratamiento tratamiento) {
        LocalDateTime ahora = LocalDateTime.now();
        tratamiento.agregarRegistroHistorial(
            "Toma OMITIDA el: " + ahora.toLocalDate() + " a las " + ahora.toLocalTime()
        );
        tratamiento.registrarTomaOmitida();
        this.paciente.verificarCumplimiento();
    }

    public List<Tratamiento> chequearAlarmasPendientes() {
        List<Tratamiento> pendientes = new ArrayList<>();
        for (Tratamiento t : paciente.getTratamientos()) {
            if (t.verificarAlertaHorario()) {
                pendientes.add(t);
            }
        }
        return pendientes;
    }
}
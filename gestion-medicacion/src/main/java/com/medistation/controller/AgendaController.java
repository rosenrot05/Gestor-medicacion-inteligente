/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.controller;

/**
 *
 * @author Rosa
 */
import com.medistation.model.Paciente;
import com.medistation.model.Tratamiento;
import com.medistation.strategy.ValidadorTratamiento; // Paquete estratégico de validación

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
        // Analizamos el tratamiento "suelto" contra los tratamientos que el paciente YA tiene activos.
        for (ValidadorTratamiento validador : validadoresActivos) {
            if (!validador.validar(nuevo, paciente.getTratamientos())) {
                System.out.println("[RECHAZADO] No se pudo agendar el tratamiento debido a un conflicto médico.");
                return false; // Se corta el método y NO se guarda nada.
            }
        }

        // Si el ciclo 'for' terminó y ningún validador devolvió 'false', significa que es 100% seguro.
        this.paciente.agregarTratamiento(nuevo);

        System.out.println("[ÉXITO] Tratamiento validado y agendado correctamente.");
        return true;
    }

    public void registrarToma(Tratamiento tratamiento) {
        // 1. Guardamos el registro real con la hora actual
        LocalDateTime horaActual = LocalDateTime.now();
        tratamiento.agregarRegistroHistorial("Toma normal registrada el: " + horaActual.toLocalDate() + " a las " + horaActual.toLocalTime());
        
        // 2. La clase Tratamiento hace sus cálculos
        tratamiento.registrarTomaEjecutada();
        
        // 3. Recalculamos cumplimiento
        this.paciente.verificarCumplimiento();
    }
    
    public void registrarToma(Tratamiento tratamiento, LocalDateTime horaManual) {
        // 1. Guardamos el registro real indicando que fue manual
        tratamiento.agregarRegistroHistorial(" Toma MANUAL registrada para el: " + horaManual.toLocalDate() + " a las " + horaManual.toLocalTime());
        
        // 2. Reutilizamos la lógica de cálculos
        tratamiento.registrarTomaEjecutada();
        this.paciente.verificarCumplimiento();
    }
    
    public void omitirToma(Tratamiento tratamiento) {
        // ¡También es buena idea registrar si se la saltó!
        LocalDateTime horaActual = LocalDateTime.now();
        tratamiento.agregarRegistroHistorial("Toma OMITIDA el: " + horaActual.toLocalDate() + " a las " + horaActual.toLocalTime());
        
        tratamiento.registrarTomaOmitida();
        this.paciente.verificarCumplimiento();
    }
    
    public List<Tratamiento> chequearAlarmasPendientes() {
        List<Tratamiento> pendientes = new ArrayList<>();
        
        for (Tratamiento t : paciente.getTratamientos()) {
            // Usamos el método correcto que definiste en Tratamiento
            if (t.verificarAlertaHorario()) {
                pendientes.add(t);
            }
        }
        return pendientes;
    }
}

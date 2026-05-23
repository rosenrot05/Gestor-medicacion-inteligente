package com.medistation.controller;

import com.medistation.model.Paciente;
import com.medistation.model.TipoDiscapacidad;
import java.util.ArrayList;
import java.util.List;

public class PacienteController {
    private final List<Paciente> pacientesRegistrados;
    private Paciente pacienteActual;

    public PacienteController() {
        this.pacientesRegistrados = new ArrayList<>();
        this.pacienteActual = null;
    }

    public void registrarPaciente(Paciente nuevoPaciente) {
        if (nuevoPaciente == null) {
            throw new IllegalArgumentException("El paciente a registrar no puede ser nulo.");
        }
        for (Paciente p : pacientesRegistrados) {
            if (p.getNombre().equalsIgnoreCase(nuevoPaciente.getNombre())) {
                throw new IllegalArgumentException("Ya existe un paciente con el nombre: " + nuevoPaciente.getNombre());
            }
        }
        this.pacientesRegistrados.add(nuevoPaciente);
    }

    public void registrarPaciente(String nombre, int edad, double peso) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
        Paciente nuevo = new Paciente(nombre, edad);
        nuevo.getPerfil().setPeso(peso);
        this.registrarPaciente(nuevo);
    }

    public boolean seleccionarPaciente(String nombre) {
        for (Paciente p : pacientesRegistrados) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                this.pacienteActual = p;
                return true;
            }
        }
        return false;
    }

    // borra el paciente y limpia sesion si era el activo
    public boolean eliminarPaciente(String nombre) {
        Paciente objetivo = null;
        for (Paciente p : pacientesRegistrados) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                objetivo = p;
                break;
            }
        }
        
        if (objetivo == null) {
            return false;
        }
        
        pacientesRegistrados.remove(objetivo);
        
        if (pacienteActual != null && pacienteActual.getNombre().equalsIgnoreCase(nombre)) {
            pacienteActual = null;
        }
        
        return true;
    }

    public void agregarAlergia(String alergia) {
        if (pacienteActual == null) {
            throw new IllegalStateException("No hay un paciente seleccionado.");
        }
        pacienteActual.getPerfil().agregarAlergia(alergia);
    }

    public void activarDiscapacidad(TipoDiscapacidad tipo) {
        if (pacienteActual == null) {
            throw new IllegalStateException("No hay un paciente seleccionado.");
        }
        pacienteActual.getPerfil().agregarDiscapacidad(tipo);
    }

    public double obtenerReporteAdherencia() {
        if (pacienteActual == null) {
            throw new IllegalStateException("No hay un paciente seleccionado.");
        }
        return pacienteActual.calcularAdherenciaGeneral();
    }

    public Paciente getPacienteActual() {
        return this.pacienteActual;
    }

    public List<Paciente> getPacientesRegistrados() {
        return new ArrayList<>(this.pacientesRegistrados);
    }
}
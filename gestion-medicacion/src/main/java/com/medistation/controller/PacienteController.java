/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.controller;

import com.medistation.model.Paciente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Rosa
 */
public class PacienteController {
    private final List<Paciente> pacientesRegistrados;
    private Paciente pacienteActual;
    
    public PacienteController() {
        this.pacientesRegistrados = new ArrayList<>();
        this.pacienteActual = null;
    }
    
    public void registrarPaciente(Paciente nuevoPaciente){
        if(nuevoPaciente == null){
            throw new IllegalArgumentException("El paciente a registrar no puede ser nulo");
        }
        
        boolean existe = pacientesRegistrados.stream()
                                            .anyMatch(p -> p.getNombre().equalsIgnoreCase(nuevoPaciente.getNombre()));
        if(existe){
            throw new IllegalArgumentException("Ya existe un paciente registrado con el nombre: " + nuevoPaciente.getNombre());
        }
        
        this.pacientesRegistrados.add(nuevoPaciente);
    }
    
    public boolean seleccionarPaciente(String nombre) {
        Optional<Paciente> pacienteEncontrado = pacientesRegistrados.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
                
        if (pacienteEncontrado.isPresent()) {
            this.pacienteActual = pacienteEncontrado.get();
            return true;
        }
        return false;
    }
    
    public Paciente getPacienteActual() {
        return this.pacienteActual;
    }
    
    public List<Paciente> getPacientesRegistrados() {
        return new ArrayList<>(this.pacientesRegistrados);
    }
    
}

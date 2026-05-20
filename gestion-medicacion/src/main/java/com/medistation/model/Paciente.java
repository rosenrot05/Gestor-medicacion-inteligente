/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rosa
 */

public class Paciente {
    private String nombre;
    private int edad;
    private PerfilClinico perfil; // Relación de Composición
    private List<Tratamiento> tratamientos; // Lado 1 de la Bidireccionalidad

    public Paciente(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        //Instanciamos el PerfilClinico directamente aquí adentro.
        // Nace y muere con el Paciente.
        this.perfil = new PerfilClinico(0.0); 
        this.tratamientos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    // Nota: Solo hay getter para el perfil, no setter, protegiendo la composición.
    public PerfilClinico getPerfil() {
        return perfil;
    }
    
    public List<Tratamiento> getTratamientos(){
        return tratamientos;
    }

    public void agregarTratamiento(Tratamiento t) {
        this.tratamientos.add(t);
    }

    public double calcularAdherenciaGeneral() {
        int totalProgramadas = 0;
        int totalCompletadas = 0;

        // Recorremos todos los tratamientos del paciente
        for (Tratamiento t : tratamientos) {
            totalProgramadas += t.getTomasProgramadas();
            totalCompletadas += t.getTomasCompletadas();
        }

        // Evitamos dividir por cero si el paciente es nuevo
        if (totalProgramadas == 0) {
            return 100.0; // Si no tiene tomas aún, su adherencia es perfecta
        }

        // Calculamos el porcentaje de éxito (Ej: 8 de 10 tomas = 80.0%)
        return ((double) totalCompletadas / totalProgramadas) * 100.0;
    }

    public double verificarCumplimiento() {
        return calcularAdherenciaGeneral();
    }
}

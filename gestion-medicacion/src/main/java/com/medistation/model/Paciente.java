package com.medistation.model;

import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private String nombre;
    private int edad;
    private PerfilClinico perfil;
    private List<Tratamiento> tratamientos;

    public Paciente(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        this.perfil = new PerfilClinico(0.0);
        this.tratamientos = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public PerfilClinico getPerfil() { return perfil; }
    
    public List<Tratamiento> getTratamientos(){ return tratamientos; }

    public void agregarTratamiento(Tratamiento t) {
        this.tratamientos.add(t);
    }

    public double calcularAdherenciaGeneral() {
        int totalProgramadas = 0;
        int totalCompletadas = 0;

        for (Tratamiento t : tratamientos) {
            totalProgramadas += t.getTomasProgramadas();
            totalCompletadas += t.getTomasCompletadas();
        }

        // evitar division por cero
        if (totalProgramadas == 0) {
            return 100.0;
        }

        return ((double) totalCompletadas / totalProgramadas) * 100.0;
    }

    public double verificarCumplimiento() {
        return calcularAdherenciaGeneral();
    }
}
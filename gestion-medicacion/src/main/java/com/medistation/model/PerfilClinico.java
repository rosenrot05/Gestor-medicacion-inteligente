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
public class PerfilClinico {
    private double peso;
    private List<String> alergias;
    private List<TipoDiscapacidad> discapacidades;

    public PerfilClinico(double peso) {
        this.peso = peso;
        // Inicializamos las listas vacías 
        this.alergias = new ArrayList<>();
        this.discapacidades = new ArrayList<>();
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public List<String> getAlergias() {
        return alergias;
    }

    public List<TipoDiscapacidad> getDiscapacidades() {
        return discapacidades;
    }

    public void agregarAlergia(String alergia) {
        this.alergias.add(alergia);
    }

    public void agregarDiscapacidad(TipoDiscapacidad discapacidad) {
        this.discapacidades.add(discapacidad);
    }
}

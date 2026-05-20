/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.controller;

import com.medistation.model.Capsula;
import com.medistation.model.Inhalador;
import com.medistation.model.Jarabe;
import com.medistation.model.Medicamento;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rosa
 */
public class InventarioController {
    
    private final List<Medicamento> catalogoMedicamentos;
    private static final double UMBRAL_ALERTA_DEFECTO = 10.0; // Para usuarios que no lo configuran
    
    public InventarioController() {
        this.catalogoMedicamentos = new ArrayList<>();
    }
    
   
    // MÉTODOS DE REGISTRO RÁPIDO (Usan el valor por defecto)

    
    public void registrarCapsula(String nombre, String principio, double stock) {
        Medicamento capsula = new Capsula(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO);
        agregarAlCatalogo(capsula);
    }
    
    public void registrarJarabe(String nombre, String principio, double stock) {
        Medicamento jarabe = new Jarabe(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO);
        agregarAlCatalogo(jarabe);
    }
    
    public void registrarInhalador(String nombre, String principio, double stock) {
        Medicamento inhalador = new Inhalador(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO);
        agregarAlCatalogo(inhalador);
    }


    // SOBRECARGA DE MÉTODOS: REGISTRO PERSONALIZADO (El usuario elige el umbral)

    
    public void registrarCapsula(String nombre, String principio, double stock, double umbralPersonalizado) {
        Medicamento capsula = new Capsula(nombre, principio, stock, umbralPersonalizado);
        agregarAlCatalogo(capsula);
    }
    
    public void registrarJarabe(String nombre, String principio, double stock, double umbralPersonalizado) {
        Medicamento jarabe = new Jarabe(nombre, principio, stock, umbralPersonalizado);
        agregarAlCatalogo(jarabe);
    }
    
    public void registrarInhalador(String nombre, String principio, double stock, double umbralPersonalizado) {
        Medicamento inhalador = new Inhalador(nombre, principio, stock, umbralPersonalizado);
        agregarAlCatalogo(inhalador);
    }


    // RESTO DE MÉTODOS DEL CONTROLADOR


    public Medicamento buscarMedicamento(String nombre) {
        for (Medicamento m : catalogoMedicamentos) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                return m;
            }
        }
        return null; // Retorna null si no lo encuentra
    }

    public void reabastecerMedicamento(String nombre, double cantidad) {
        Medicamento medicamento = buscarMedicamento(nombre);
        if (medicamento != null) {
            medicamento.actualizarStock(cantidad, "Reabastecimiento manual");
        } else {
            throw new IllegalArgumentException("No es posible reabastecer. El fármaco no existe.");
        }
    }

    public List<Medicamento> obtenerAlertasBajoStock() {
        List<Medicamento> alertas = new ArrayList<>();
        for (Medicamento m : catalogoMedicamentos) {
            if (m.alertarBajoStock()) {
                alertas.add(m);
            }
        }
        return alertas;
    }

    private void agregarAlCatalogo(Medicamento m) {
        if (buscarMedicamento(m.getNombre()) != null) {
            throw new IllegalStateException("El medicamento ya se encuentra registrado.");
        }
        this.catalogoMedicamentos.add(m);
    }
}

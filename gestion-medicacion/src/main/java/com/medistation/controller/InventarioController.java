package com.medistation.controller;

import com.medistation.model.Capsula;
import com.medistation.model.Inhalador;
import com.medistation.model.Jarabe;
import com.medistation.model.Medicamento;
import java.util.ArrayList;
import java.util.List;

public class InventarioController {

    private final List<Medicamento> catalogoMedicamentos;
    private static final double UMBRAL_ALERTA_DEFECTO = 10.0;

    public InventarioController() {
        this.catalogoMedicamentos = new ArrayList<>();
    }

    // Registro rápido (umbral por defecto)
    public void registrarCapsula(String nombre, String principio, double stock) {
        agregarAlCatalogo(new Capsula(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO));
    }

    public void registrarJarabe(String nombre, String principio, double stock) {
        agregarAlCatalogo(new Jarabe(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO));
    }

    public void registrarInhalador(String nombre, String principio, double stock) {
        agregarAlCatalogo(new Inhalador(nombre, principio, stock, UMBRAL_ALERTA_DEFECTO));
    }

    // Registro personalizado (sobrecarga con umbral propio)
    public void registrarCapsula(String nombre, String principio, double stock, double umbralPersonalizado) {
        agregarAlCatalogo(new Capsula(nombre, principio, stock, umbralPersonalizado));
    }

    public void registrarJarabe(String nombre, String principio, double stock, double umbralPersonalizado) {
        agregarAlCatalogo(new Jarabe(nombre, principio, stock, umbralPersonalizado));
    }

    public void registrarInhalador(String nombre, String principio, double stock, double umbralPersonalizado) {
        agregarAlCatalogo(new Inhalador(nombre, principio, stock, umbralPersonalizado));
    }

    public Medicamento buscarMedicamento(String nombre) {
        for (Medicamento m : catalogoMedicamentos) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                return m;
            }
        }
        return null;
    }

    public void reabastecerMedicamento(String nombre, double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser mayor a cero.");
        }
        Medicamento medicamento = buscarMedicamento(nombre);
        if (medicamento != null) {
            medicamento.actualizarStock(cantidad, "Reabastecimiento manual");
        } else {
            throw new IllegalArgumentException("No es posible reabastecer. El fármaco '" + nombre + "' no existe.");
        }
    }

    // Nota: el UML dice List<String> pero devolver List<Medicamento> es más útil para la vista
    public List<Medicamento> obtenerAlertasBajoStock() {
        List<Medicamento> alertas = new ArrayList<>();
        for (Medicamento m : catalogoMedicamentos) {
            if (m.alertarBajoStock()) {
                alertas.add(m);
            }
        }
        return alertas;
    }

    public List<Medicamento> getCatalogoMedicamentos() {
        return new ArrayList<>(catalogoMedicamentos);
    }

    private void agregarAlCatalogo(Medicamento m) {
        if (buscarMedicamento(m.getNombre()) != null) {
            throw new IllegalStateException("El medicamento '" + m.getNombre() + "' ya está registrado.");
        }
        this.catalogoMedicamentos.add(m);
    }
}
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

    public void registrarCapsula(String nombre, String principio, int envases, double contenidoPorEnvase) {
        agregarAlCatalogo(new Capsula(nombre, principio, envases, contenidoPorEnvase, UMBRAL_ALERTA_DEFECTO));
    }

    public void registrarCapsula(String nombre, String principio, int envases, double contenidoPorEnvase, double umbral) {
        agregarAlCatalogo(new Capsula(nombre, principio, envases, contenidoPorEnvase, umbral));
    }

    public void registrarJarabe(String nombre, String principio, int envases, double contenidoPorEnvase) {
        agregarAlCatalogo(new Jarabe(nombre, principio, envases, contenidoPorEnvase));
    }

    public void registrarInhalador(String nombre, String principio, int envases, double contenidoPorEnvase) {
        agregarAlCatalogo(new Inhalador(nombre, principio, envases, contenidoPorEnvase));
    }

    public Medicamento buscarMedicamento(String nombre) {
        for (Medicamento m : catalogoMedicamentos) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                return m;
            }
        }
        return null;
    }

    public void reabastecerMedicamento(String nombre, int nuevosEnvases) {
        if (nuevosEnvases <= 0) {
            throw new IllegalArgumentException("La cantidad de envases no puede ser negativa.");
        }
        Medicamento m = buscarMedicamento(nombre);
        if (m == null) {
            throw new IllegalArgumentException("El fármaco '" + nombre + "' no existe.");
        }
        m.actualizarEnvases(nuevosEnvases);
    }

    // elimina por nombre y avisa si existia
    public boolean eliminarMedicamento(String nombre) {
        Medicamento objetivo = buscarMedicamento(nombre);
        if (objetivo == null) {
            return false;
        }
        catalogoMedicamentos.remove(objetivo);
        return true;
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
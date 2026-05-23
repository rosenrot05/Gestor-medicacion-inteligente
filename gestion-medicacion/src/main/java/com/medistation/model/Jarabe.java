package com.medistation.model;

public class Jarabe extends Medicamento {

    public Jarabe(String nombre, String principioActivo, int envases, double mlPorEnvase) {
        // umbral en 0 porque se define dinamico por el tratamiento
        super(nombre, principioActivo, envases, mlPorEnvase, "ml", 0.0);
    }

    @Override
    public void reducirStock(double cantidad) {
        super.actualizarStock(-cantidad);
    }
}
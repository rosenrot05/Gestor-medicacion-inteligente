package com.medistation.model;

public class Capsula extends Medicamento {

    public Capsula(String nombre, String principioActivo, int envases, double pastasPorEnvase, double umbralAlerta) {
        super(nombre, principioActivo, envases, pastasPorEnvase, "cápsulas", umbralAlerta);
    }

    @Override
    public void reducirStock(double cantidad) {
        // resta unidades enteras
        super.actualizarStock(-cantidad);
    }
}
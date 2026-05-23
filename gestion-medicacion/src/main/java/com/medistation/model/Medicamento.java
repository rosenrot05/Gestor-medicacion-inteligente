package com.medistation.model;

public abstract class Medicamento {
    private String nombre;
    private String principioActivo;

    private int cantidadEnvases;
    private double contenidoPorEnvase;
    private double stockTotal; // envases * contenido

    private String unidadMedida;
    private double umbralAlerta;

    public Medicamento(String nombre, String principioActivo, int cantidadEnvases,
                       double contenidoPorEnvase, String unidadMedida, double umbralAlerta) {
        this.nombre = nombre;
        this.principioActivo = principioActivo;
        this.cantidadEnvases = cantidadEnvases;
        this.contenidoPorEnvase = contenidoPorEnvase;
        this.stockTotal = cantidadEnvases * contenidoPorEnvase;
        this.unidadMedida = unidadMedida;
        this.umbralAlerta = umbralAlerta;
    }

    public String getNombre() { return nombre; }
    public double getStock() { return stockTotal; }
    public String getUnidadMedida() { return unidadMedida; }
    public double getUmbralAlerta() { return umbralAlerta; }
    public String getPrincipioActivo() { return principioActivo; }
    public int getCantidadEnvases() { return cantidadEnvases; }
    public double getContenidoPorEnvase() { return contenidoPorEnvase; }

    public abstract void reducirStock(double cantidad);

    public void actualizarStock(double cantidad) {
        this.stockTotal += cantidad;
        if (this.contenidoPorEnvase > 0) {
            this.cantidadEnvases = (int) Math.ceil(this.stockTotal / this.contenidoPorEnvase);
        }
    }

    // actualizacion con motivo para historial
    public void actualizarStock(double cantidad, String motivo) {
        this.actualizarStock(cantidad);
        System.out.println("Stock de " + nombre + " actualizado [" + motivo + "]. Quedan: " + stockTotal + " " + unidadMedida + " (" + cantidadEnvases + " envases).");
    }

    // corrige cantidad fisica y recalcula
    public void actualizarEnvases(int nuevosEnvases) {
        this.cantidadEnvases = nuevosEnvases;
        this.stockTotal = nuevosEnvases * this.contenidoPorEnvase;
        System.out.println("Envases de " + nombre + " actualizados a " + nuevosEnvases + ". Stock: " + stockTotal + " " + unidadMedida);
    }

    // calcula alerta para liquidos basandose en las dosis
    public void configurarUmbralDinamico(double dosisTratamiento, int margenTomas) {
        this.umbralAlerta = dosisTratamiento * margenTomas;
    }

    public boolean alertarBajoStock() {
        return this.stockTotal <= this.umbralAlerta;
    }
}
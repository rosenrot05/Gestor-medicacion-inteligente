/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

/**
 *
 * @author Rosa
 */
public abstract class Medicamento {
    private String nombre;
    private String principioActivo;
    private double stock;

    public Medicamento(String nombre, String principioActivo, double stock) {
        this.nombre = nombre;
        this.principioActivo = principioActivo;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public double getStock() {
        return stock;
    }

    // Método abstracto que obligatoriamente implementarán las clases hijas
    public abstract void reducirStock(double cantidad);

    // Sobrecarga de método 1: Actualización simple de stock
    public void actualizarStock(double cantidad) {
        this.stock += cantidad;
    }

    // Sobrecarga de método 2: Actualización con registro de motivo (auditoría)
    public void actualizarStock(double cantidad, String motivo) {
        this.stock += cantidad;
        System.out.println("Stock de " + nombre + " actualizado. Motivo: " + motivo + ". Cantidad: " + cantidad);
    }

    public boolean alertarBajoStock() {
        // Umbral de alerta: menos de 10 unidades restantes
        return this.stock < 10.0;
    }
}

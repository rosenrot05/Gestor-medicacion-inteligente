/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

/**
 *
 * @author Rosa
 */
public class Jarabe extends Medicamento {

    public Jarabe(String nombre, String principioActivo, double stock, double umbralAlerta) {
        super(nombre, principioActivo, stock, "ml", umbralAlerta);
    }

    @Override
    public void reducirStock(double cantidad) {
        // En jarabes restamos mililitros que esta en double
        super.actualizarStock(-cantidad);
    }
}

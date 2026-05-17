/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

/**
 *
 * @author Rosa
 */
public class Capsula extends Medicamento {

    public Capsula(String nombre, String principioActivo, double stock, double umbralAlerta) {
        super(nombre, principioActivo, stock, "cápsulas", umbralAlerta);
    }

    @Override
    public void reducirStock(double cantidad) {
        // En cápsulas restamos unidades enteras
        super.actualizarStock(-cantidad);
    }
}

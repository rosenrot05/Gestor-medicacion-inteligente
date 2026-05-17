/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.model;

/**
 *
 * @author Rosa
 */
public class Inhalador extends Medicamento {

    public Inhalador(String nombre, String principioActivo, double stock) {
        super(nombre, principioActivo, stock);
    }

    @Override
    public void reducirStock(double cantidad) {
        // En inhaladores restamos puffs inhalados
        super.actualizarStock(-cantidad);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.strategy;
import com.medistation.model.Tratamiento;
import java.util.List;

/**
 *
 * @author Rosa
 */
public interface ValidadorTratamiento {
    
    //valida si un nuevo tratamiento puede ser agendado y si puede coexistir con los tratamientos ya existentes
    boolean validar(Tratamiento nuevo, List<Tratamiento> activos);
}

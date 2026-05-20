/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.strategy;

import com.medistation.model.Tratamiento;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Calcula la acumulación de dosis del mismo principio activo 
// en un periodo de 24 horas usando la frecuencia en horas.
public class DetectorSobredosis implements ValidadorTratamiento {

    // NUESTRA "BASE DE DATOS" EN MEMORIA
    private final Map<String, Double> baseDatosUmbrales;

    public DetectorSobredosis() {
        // Inicializamos la tabla
        baseDatosUmbrales = new HashMap<>();
        
        // Llenamos la base de datos con límites médicos reales (en miligramos)
        baseDatosUmbrales.put("paracetamol", 4000.0); // Max 4 gramos al día
        baseDatosUmbrales.put("ibuprofeno", 1200.0);  // Max 1.2 gramos al día
        baseDatosUmbrales.put("amoxicilina", 3000.0); // Max 3 gramos al día
        baseDatosUmbrales.put("loratadina", 10.0);    // Max 10 mg al día
    }

    @Override
    public boolean validar(Tratamiento nuevo, List<Tratamiento> activos) {
        // Convertimos a minúsculas para que no haya errores si escriben "Paracetamol" o "PARACETAMOL"
        String principioNuevo = nuevo.getMedicina().getPrincipioActivo().toLowerCase();
        
        // Calculamos cuántas veces al día se va a tomar este nuevo medicamento
        int tomasDiariasNuevo = 24 / nuevo.getFrecuenciaHoras();
        double dosisDiariaNueva = nuevo.getDosis() * tomasDiariasNuevo;
        
        double dosisAcumulada24h = 0;

        // Sumamos las dosis de medicamentos QUE TENGAN EL MISMO PRINCIPIO ACTIVO
        for (Tratamiento activo : activos) {
            if (activo.getMedicina().getPrincipioActivo().equalsIgnoreCase(principioNuevo)) {
                int tomasDiariasActivo = 24 / activo.getFrecuenciaHoras();
                dosisAcumulada24h += (activo.getDosis() * tomasDiariasActivo);
            }
        }

        double dosisTotalProyectada = dosisAcumulada24h + dosisDiariaNueva;
       
        // Buscamos el umbral en nuestro Map. Si el doctor receta algo que no está en la tabla, 
        // usamos getOrDefault para poner un límite genérico (ej. 1000.0) por seguridad.
        double umbralMaximoSeguro = baseDatosUmbrales.getOrDefault(principioNuevo, 1000.0); 

        if (dosisTotalProyectada > umbralMaximoSeguro) {
            System.out.println("[ALERTA] Riesgo de sobredosis de " + principioNuevo.toUpperCase() + ". "
                    + "Dosis diaria proyectada: " + dosisTotalProyectada + " " + nuevo.getMedicina().getUnidadMedida() 
                    + " (Supera límite seguro de " + umbralMaximoSeguro + ").");
            return false; // Bloquea la operación
        }

        return true;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medistation.strategy;
import com.medistation.model.Tratamiento;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author Rosa
 */
public class DetectorSomnolencia implements ValidadorTratamiento {
    // La estrategia guarda la lista de principios activos peligrosos 
    private final List<String> principiosSedantes = Arrays.asList(
            "clonazepam", "diazepam", "lorazepam", "cetirizina", "amitriptilina"
    );
    
    @Override
    public boolean validar(Tratamiento nuevo, List<Tratamiento> activos) {
        String principioNuevo = nuevo.getMedicina().getPrincipioActivo().toLowerCase();

        // Si el nuevo medicamento NO está en la lista de sedantes, pasa la prueba
        if (!principiosSedantes.contains(principioNuevo)) {
            return true;
        }

        // Si es sedante, buscamos si hay OTRO sedante agendado a la misma hora
        for (Tratamiento activo : activos) {
            String principioActivo = activo.getMedicina().getPrincipioActivo().toLowerCase();
            
            if (principiosSedantes.contains(principioActivo)) {
                // Verificamos si las horas de la próxima toma coinciden
                if (nuevo.getProximaToma().getHour() == activo.getProximaToma().getHour()) {
                    System.out.println("[ALERTA] Conflicto de somnolencia detectado entre " 
                            + nuevo.getMedicina().getNombre() + " y " 
                            + activo.getMedicina().getNombre() + " a las " 
                            + nuevo.getProximaToma().getHour() + ":00 horas.");
                    return false; // Bloquea el tratamiento
                }
            }
        }
        return true;
    }
}


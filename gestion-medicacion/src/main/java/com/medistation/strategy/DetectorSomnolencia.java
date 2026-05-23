package com.medistation.strategy;

import com.medistation.model.Tratamiento;
import java.util.Arrays;
import java.util.List;

public class DetectorSomnolencia implements ValidadorTratamiento {
    
    // lista interna de medicamentos sedantes controlados
    private final List<String> principiosSedantes = Arrays.asList(
            "clonazepam", "diazepam", "lorazepam", "cetirizina", "amitriptilina"
    );
    
    @Override
    public boolean validar(Tratamiento nuevo, List<Tratamiento> activos) {
        String principioNuevo = nuevo.getMedicina().getPrincipioActivo().toLowerCase();

        if (!principiosSedantes.contains(principioNuevo)) {
            return true;
        }

        // busca choques de horario con otros sedantes
        for (Tratamiento activo : activos) {
            String principioActivo = activo.getMedicina().getPrincipioActivo().toLowerCase();
            
            if (principiosSedantes.contains(principioActivo)) {
                if (nuevo.getProximaToma().getHour() == activo.getProximaToma().getHour()) {
                    System.out.println("[ALERTA] Conflicto de somnolencia entre " 
                            + nuevo.getMedicina().getNombre() + " y " 
                            + activo.getMedicina().getNombre() + " a las " 
                            + nuevo.getProximaToma().getHour() + ":00 horas.");
                    return false;
                }
            }
        }
        return true;
    }
}
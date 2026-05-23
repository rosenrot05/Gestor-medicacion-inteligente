package com.medistation.strategy;

import com.medistation.model.Tratamiento;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectorSobredosis implements ValidadorTratamiento {

    private final Map<String, Double> baseDatosUmbrales;

    public DetectorSobredosis() {
        baseDatosUmbrales = new HashMap<>();
        baseDatosUmbrales.put("paracetamol", 4000.0);
        baseDatosUmbrales.put("ibuprofeno", 1200.0);
        baseDatosUmbrales.put("amoxicilina", 3000.0);
        baseDatosUmbrales.put("loratadina", 10.0);
    }

    @Override
    public boolean validar(Tratamiento nuevo, List<Tratamiento> activos) {
        // protege contra division por cero
        if (nuevo.getFrecuenciaHoras() <= 0) {
            System.out.println("[ERROR] La frecuencia del tratamiento no puede ser 0 o negativa.");
            return false;
        }

        String principioNuevo = nuevo.getMedicina().getPrincipioActivo().toLowerCase();
        int tomasDiariasNuevo = 24 / nuevo.getFrecuenciaHoras();
        double dosisDiariaNueva = nuevo.getDosis() * tomasDiariasNuevo;

        double dosisAcumulada24h = 0;

        for (Tratamiento activo : activos) {
            if (activo.getMedicina().getPrincipioActivo().equalsIgnoreCase(principioNuevo)) {
                if (activo.getFrecuenciaHoras() > 0) {
                    int tomasDiariasActivo = 24 / activo.getFrecuenciaHoras();
                    dosisAcumulada24h += (activo.getDosis() * tomasDiariasActivo);
                }
            }
        }

        double dosisTotalProyectada = dosisAcumulada24h + dosisDiariaNueva;
        double umbralMaximoSeguro = baseDatosUmbrales.getOrDefault(principioNuevo, 1000.0);

        if (dosisTotalProyectada > umbralMaximoSeguro) {
            System.out.println("[ALERTA] Riesgo de sobredosis de " + principioNuevo.toUpperCase() + ". "
                    + "Dosis proyectada: " + dosisTotalProyectada + " "
                    + nuevo.getMedicina().getUnidadMedida()
                    + " (Límite: " + umbralMaximoSeguro + ").");
            return false;
        }

        return true;
    }
}
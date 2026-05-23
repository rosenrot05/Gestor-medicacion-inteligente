package com.medistation.pruebas;

import com.medistation.controller.AgendaController;
import com.medistation.model.Capsula;
import com.medistation.model.Paciente;
import com.medistation.model.Tratamiento;
import com.medistation.strategy.DetectorSobredosis;
import java.time.LocalDateTime;

public class PruebaIntegracionAgenda {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DE INTEGRACION: AGENDA Y SEGURIDAD ===");
        
        // setup inicial
        Paciente paciente = new Paciente("Juan Perez", 45);
        AgendaController agenda = new AgendaController(paciente);
        
        // agregar validador
        agenda.agregarReglaValidacion(new DetectorSobredosis());
        
        // limite paracetamol es 4000mg
        Capsula paracetamol = new Capsula("Dolex", "paracetamol", 5, 10, 5.0);

        // prueba tratamiento seguro
        System.out.println("\nCaso 1: Agendar dosis normal (1000mg cada 8 horas = 3000mg/dia).");
        Tratamiento tratamientoSeguro = new Tratamiento(paciente, paracetamol, 8, 1000.0, LocalDateTime.now());
        boolean agendadoNormal = agenda.intentarAgendarTratamiento(tratamientoSeguro);
        
        if (agendadoNormal && paciente.getTratamientos().size() == 1) {
            System.out.println("  -> [EXITO] El sistema permitio el tratamiento seguro.");
        } else {
            System.out.println("  -> [FALLO] El sistema bloqueo un tratamiento seguro.");
        }

        // prueba bloqueo por sobredosis
        System.out.println("\nCaso 2: Intentar agendar otra receta del mismo principio activo (1000mg extra cada 6h).");
        System.out.println("  (Proyeccion: 3000mg + 4000mg = 7000mg/dia. Debe bloquear).");
        
        Tratamiento tratamientoPeligroso = new Tratamiento(paciente, paracetamol, 6, 1000.0, LocalDateTime.now());
        boolean agendadoSobredosis = agenda.intentarAgendarTratamiento(tratamientoPeligroso);
        
        if (!agendadoSobredosis && paciente.getTratamientos().size() == 1) {
            System.out.println("  -> [EXITO] El validador bloqueo la sobredosis.");
        } else {
            System.out.println("  -> [FALLO] ALERTA: El sistema permitio la sobredosis.");
        }
        
        System.out.println("\n=== FIN DE PRUEBA DE INTEGRACION ===");
    }
}
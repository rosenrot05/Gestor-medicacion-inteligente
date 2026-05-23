package com.medistation.pruebas;

import com.medistation.controller.PacienteController;
import com.medistation.model.Paciente;
import com.medistation.model.TipoDiscapacidad;

public class PruebaUnitariaPaciente {

    public static void main(String[] args) {
        System.out.println("INICIANDO PRUEBAS UNITARIAS: PACIENTES");
        
        // setup
        PacienteController pacienteCtrl = new PacienteController();
        
        // prueba 1: registro de paciente
        System.out.println("\nPrueba 1: Registrar un paciente nuevo.");
        pacienteCtrl.registrarPaciente("Ana Lopez", 30, 65.5);
        
        if (pacienteCtrl.getPacientesRegistrados().size() == 1) {
            System.out.println("  -> [EXITO] Paciente registrado en la lista de forma correcta.");
        } else {
            System.out.println("  -> [FALLO] El paciente no se guardo.");
        }
        
        // prueba 2: seleccion y modificacion del perfil clinico
        System.out.println("\nPrueba 2: Seleccionar paciente y agregar datos medicos.");
        boolean seleccionado = pacienteCtrl.seleccionarPaciente("Ana Lopez");
        
        if (seleccionado) {
            pacienteCtrl.agregarAlergia("Penicilina");
            pacienteCtrl.activarDiscapacidad(TipoDiscapacidad.VISUAL_PARCIAL);
            
            Paciente activo = pacienteCtrl.getPacienteActual();
            
            if (activo != null && activo.getPerfil().getAlergias().contains("Penicilina")) {
                System.out.println("  -> [EXITO] Sesion vinculada y datos medicos guardados.");
            } else {
                System.out.println("  -> [FALLO] Los datos medicos no se guardaron en el perfil.");
            }
        } else {
            System.out.println("  -> [FALLO] No se pudo seleccionar al paciente.");
        }
        
        // prueba 3: eliminar paciente y verificar que la sesion se limpia
        System.out.println("\nPrueba 3: Eliminar paciente activo y comprobar seguridad de sesion.");
        pacienteCtrl.eliminarPaciente("Ana Lopez");
        
        if (pacienteCtrl.getPacientesRegistrados().isEmpty() && pacienteCtrl.getPacienteActual() == null) {
            System.out.println("  -> [EXITO] Paciente borrado del sistema y sesion limpiada.");
        } else {
            System.out.println("  -> [FALLO] El paciente sigue existiendo o la sesion quedo abierta.");
        }
        
        System.out.println("\n=== FIN DE PRUEBAS UNITARIAS ===");
    }
}
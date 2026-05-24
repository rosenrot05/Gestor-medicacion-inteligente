package com.medistation.model;
import com.medistation.controller.InventarioController;
import com.medistation.controller.PacienteController;


public class GeneradorDatosPrueba {

    public static void inicializarSistema(PacienteController pacienteCtrl, InventarioController invCtrl) {
        
        //intentar cargar absolutamente todo desde el TXT a los controladores
        GestorArchivosTXT.cargarDatos(pacienteCtrl, invCtrl);
        
        //si no hay pacientes registrados Y el inventario esta vacio, es la primera vez que se abre
        if (pacienteCtrl.getPacientesRegistrados().isEmpty() && invCtrl.getCatalogoMedicamentos().isEmpty()) {
            System.out.println("TXT vacio o inexistente. Generando datos de prueba automaticos...");
            
            //generar pacientes de prueba
            pacienteCtrl.registrarPaciente("Carlos Martinez", 65, 70.5);
            pacienteCtrl.registrarPaciente("Lucia Fernandez", 42, 60.0);
            pacienteCtrl.registrarPaciente("Roberto Gomez", 80, 75.2);
            
            //generar inventario de prueba global
            invCtrl.registrarCapsula("Dolex Forte", "Paracetamol", 5, 20.0);
            invCtrl.registrarCapsula("Advil", "Ibuprofeno", 3, 15.0);
            
            //guardar todo inmediatamente usando la nueva firma
            GestorArchivosTXT.guardarDatos(pacienteCtrl, invCtrl);
        } else {
            System.out.println("Datos cargados exitosamente desde el bloc de notas.");
        }
    }
}
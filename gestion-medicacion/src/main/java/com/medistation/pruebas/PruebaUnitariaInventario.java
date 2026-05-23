package com.medistation.pruebas;

import com.medistation.controller.InventarioController;
import com.medistation.model.Medicamento;

public class PruebaUnitariaInventario {

    public static void main(String[] args) {
        System.out.println("INICIANDO PRUEBAS UNITARIAS: INVENTARIO");
        
        // setup
        InventarioController inventario = new InventarioController();
        
        // prueba registro y stock inicial
        System.out.println("\nPrueba 1: Registrar medicamento y verificar stock.");
        // 2 envases x 10 pastillas = 20
        inventario.registrarCapsula("Ibuprofeno", "Ibuprofeno", 2, 10.0);
        
        Medicamento med = inventario.buscarMedicamento("Ibuprofeno");
        
        if (med != null && med.getStock() == 20.0) {
            System.out.println("  -> [EXITO] Medicamento guardado, stock = 20.0");
        } else {
            System.out.println("  -> [FALLO] Error en registro o calculo de stock.");
        }
        
        // prueba reabastecimiento
        System.out.println("\nPrueba 2: Reabastecer el medicamento a 5 envases.");
        // actualiza a 5 envases
        inventario.reabastecerMedicamento("Ibuprofeno", 5);
        
        if (med.getCantidadEnvases() == 5 && med.getStock() == 50.0) {
            System.out.println("  -> [EXITO] Reabastecimiento ok, 5 envases = 50.0");
        } else {
            System.out.println("  -> [FALLO] Error matematico al reabastecer.");
        }
        
        System.out.println("\n=== FIN DE PRUEBAS UNITARIAS ===");
    }
}
package com.medistation.model; 

import com.medistation.controller.InventarioController;
import com.medistation.controller.PacienteController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivosTXT {

    private static final String RUTA_ARCHIVO = "datos_medistation.txt";

    public static void guardarDatos(PacienteController pacienteCtrl, InventarioController invCtrl) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            
            writer.write("=== PACIENTES ===\n");
            for (Paciente p : pacienteCtrl.getPacientesRegistrados()) {
                double peso = 0.0;
                String strAlergias = "";
                String strDiscapacidades = "";

                if (p.getPerfil() != null) {
                    peso = p.getPerfil().getPeso();
                    
                    if (!p.getPerfil().getAlergias().isEmpty()) {
                        strAlergias = String.join(",", p.getPerfil().getAlergias());
                    }
                    
                    if (!p.getPerfil().getDiscapacidades().isEmpty()) {
                        List<String> discNames = new ArrayList<>();
                        for (TipoDiscapacidad td : p.getPerfil().getDiscapacidades()) {
                            discNames.add(td.name());
                        }
                        strDiscapacidades = String.join(",", discNames);
                    }
                }
                
                writer.write(p.getNombre() + ";" + p.getEdad() + ";" + peso + ";" + strAlergias + ";" + strDiscapacidades + "\n");
            }

            writer.write("=== TRATAMIENTOS ===\n");
            for (Paciente p : pacienteCtrl.getPacientesRegistrados()) {
                if (p.getTratamientos() != null) {
                    for (Tratamiento t : p.getTratamientos()) {
                        writer.write(p.getNombre() + ";" + t.getMedicamento().getNombre() + ";" + 
                                     t.getFrecuenciaHoras() + ";" + t.getDosis() + "\n");
                    }
                }
            }
            
            writer.write("=== INVENTARIO ===\n");
            if (invCtrl != null && invCtrl.getCatalogoMedicamentos() != null) {
                for (Medicamento m : invCtrl.getCatalogoMedicamentos()) {
                    String tipo = m.getClass().getSimpleName(); 
                    writer.write(m.getNombre() + ";" + m.getPrincipioActivo() + ";" + 
                                 m.getStock() + ";" + m.getUmbralAlerta() + ";" + tipo + "\n");
                }
            }
            
            System.out.println("Datos guardados correctamente en: " + RUTA_ARCHIVO);
            
        } catch (Exception e) {
            System.err.println("Error al guardar en TXT: " + e.getMessage());
        }
    }

    public static void cargarDatos(PacienteController pacienteCtrl, InventarioController invCtrl) {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) return; 

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                if (linea.startsWith("===")) {
                    seccionActual = linea;
                    continue;
                }
                
                String[] partes = linea.split(";");
                
                try {
                    if (seccionActual.equals("=== PACIENTES ===") && partes.length >= 3) {
                        String nombre = partes[0];
                        int edad = Integer.parseInt(partes[1]);
                        double peso = Double.parseDouble(partes[2]);
                        
                        pacienteCtrl.registrarPaciente(nombre, edad, peso);
                        pacienteCtrl.seleccionarPaciente(nombre);
                        
                        if (partes.length >= 4 && !partes[3].isEmpty()) {
                            String[] alergias = partes[3].split(",");
                            for (String a : alergias) {
                                pacienteCtrl.agregarAlergia(a);
                            }
                        }
                        
                        if (partes.length >= 5 && !partes[4].isEmpty()) {
                            String[] discapacidades = partes[4].split(",");
                            for (String d : discapacidades) {
                                pacienteCtrl.activarDiscapacidad(TipoDiscapacidad.valueOf(d));
                            }
                        }
                    } 
                    else if (seccionActual.equals("=== TRATAMIENTOS ===") && partes.length >= 4 && invCtrl != null) {
                        String nombrePaciente = partes[0];
                        String nombreMed = partes[1];
                        int frecuencia = Integer.parseInt(partes[2]);
                        double dosis = Double.parseDouble(partes[3]);
                        
                        pacienteCtrl.seleccionarPaciente(nombrePaciente);
                        Paciente p = pacienteCtrl.getPacienteActual();
                        Medicamento m = invCtrl.buscarMedicamento(nombreMed);
                        
                        if (p != null && m != null) {
                            Tratamiento t = new Tratamiento(p, m, frecuencia, dosis, LocalDateTime.now());
                            p.agregarTratamiento(t);
                        }
                    }
                    else if (seccionActual.equals("=== INVENTARIO ===") && partes.length >= 4 && invCtrl != null) {
                        String nombre = partes[0];
                        String principio = partes[1];
                        double stock = Double.parseDouble(partes[2]);
                        double umbral = Double.parseDouble(partes[3]);
                        String tipo = partes.length >= 5 ? partes[4] : "Capsula";
                        
                        if (tipo.equalsIgnoreCase("Jarabe")) {
                            invCtrl.registrarJarabe(nombre, principio, (int) stock, umbral);
                        } else if (tipo.equalsIgnoreCase("Inhalador")) {
                            invCtrl.registrarInhalador(nombre, principio, (int) stock, umbral);
                        } else {
                            invCtrl.registrarCapsula(nombre, principio, (int) stock, umbral);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error leyendo una linea del TXT: " + linea + " | Causa: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer el TXT: " + e.getMessage());
        }
    }
}
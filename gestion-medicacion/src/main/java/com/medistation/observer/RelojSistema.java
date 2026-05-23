package com.medistation.observer;

import com.medistation.model.Tratamiento;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;


public class RelojSistema {
    private final List<ObservadorAlarma> observadores;
    private List<Tratamiento> tratamientosActivos;
    private boolean ejecutando;

    public RelojSistema() {
        this.observadores = new ArrayList<>();
        this.tratamientosActivos = new ArrayList<>();
        this.ejecutando = false;
    }


    public void suscribirObservador(ObservadorAlarma obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }


    public void setTratamientosActivos(List<Tratamiento> tratamientos) {
        this.tratamientosActivos = tratamientos;
    }


    public void iniciarReloj() {
        if (ejecutando) return;
        this.ejecutando = true;
        
        Thread hiloReloj = new Thread(() -> {
            while (ejecutando) {
                comprobarHorarios();
                try {
                    // Pausa de 1 minuto (60000 ms) entre comprobaciones.
                    Thread.sleep(60000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        hiloReloj.setDaemon(true); // Permite que el hilo muera si se cierra la app
        hiloReloj.start();
    }

 
    private void comprobarHorarios() {
        LocalDateTime ahora = LocalDateTime.now();
        for (Tratamiento t : tratamientosActivos) {
            // Verifica que haya una próxima toma y que ya sea la hora
            if (t.getProximaToma() != null && !ahora.isBefore(t.getProximaToma())) {
                notificarObservadores(t);
            }
        }
    }

 
    private void notificarObservadores(Tratamiento t) {
        SwingUtilities.invokeLater(() -> {
            for (ObservadorAlarma obs : observadores) {
                obs.onAlarmaDisparada(t);
            }
        });
    }
}
package com.medistation.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.medistation.controller.PacienteController;
import com.medistation.model.Tratamiento;

// historial de tomas del paciente activo
public class HistorialFrame extends JFrame {

    private final PacienteController pacienteCtrl;
    private JTextArea txtHistorial;

    public HistorialFrame(PacienteController pacienteCtrl) {
        this.pacienteCtrl = pacienteCtrl;

        setTitle("MediStation — Historial de Tomas");
        setSize(620, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("Historial de cumplimiento y auditoría de tomas");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 0));
        add(titulo, BorderLayout.NORTH);

        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHistorial.setBackground(new Color(245, 247, 250));
        add(new JScrollPane(txtHistorial), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarHistorial());
        add(btnActualizar, BorderLayout.SOUTH);
    }

    public void cargarHistorial() {
        if (pacienteCtrl == null || pacienteCtrl.getPacienteActual() == null) {
            txtHistorial.setText("No hay paciente seleccionado.");
            return;
        }

        List<Tratamiento> tratamientos = pacienteCtrl.getPacienteActual().getTratamientos();
        if (tratamientos.isEmpty()) {
            txtHistorial.setText("El paciente no tiene tratamientos registrados.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================\n");
        sb.append(" PACIENTE: ").append(pacienteCtrl.getPacienteActual().getNombre().toUpperCase()).append("\n");
        sb.append("==============================================================\n\n");

        for (Tratamiento t : tratamientos) {
            sb.append("  Medicamento : ").append(t.getMedicina().getNombre()).append("\n");
            sb.append("  Completadas : ").append(t.getTomasCompletadas())
              .append(" / Programadas: ").append(t.getTomasProgramadas()).append("\n");
            sb.append("  Bitácora:\n");
            List<String> logs = t.getHistorialTomas();
            if (logs.isEmpty()) {
                sb.append("    -> Sin eventos aún.\n");
            } else {
                for (String log : logs) {
                    sb.append("    -> ").append(log).append("\n");
                }
            }
            sb.append("--------------------------------------------------------------\n\n");
        }
        txtHistorial.setText(sb.toString());
        txtHistorial.setCaretPosition(0);
    }
}
package com.medistation.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.medistation.controller.AgendaController;
import com.medistation.controller.PacienteController;
import com.medistation.model.Tratamiento;

// muestra la proxima toma y gestiona su registro
public class DashboardFrame extends JFrame {

    private PacienteController pacienteCtrl;
    private AgendaController agendaCtrl;
    private Tratamiento tratamientoActual;

    private JLabel lblPaciente;
    private JLabel lblMedicamento;
    private JLabel lblHora;
    private JLabel lblDosis;
    private JLabel lblAdherencia;
    
    private JButton btnTomar;
    private JButton btnManual;
    private JButton btnSaltar;

    public DashboardFrame(PacienteController pacienteCtrl, AgendaController agendaCtrl) {
        this.pacienteCtrl = pacienteCtrl;
        this.agendaCtrl = agendaCtrl;

        setTitle("MediStation — Dashboard");
        setSize(560, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(construirBanner(), BorderLayout.NORTH);
        add(construirPanelCentral(), BorderLayout.CENTER);
        
        configurarBotones();
        actualizarVista();
    }

    private JPanel construirBanner() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setBackground(new Color(230, 126, 34));
        lblPaciente = new JLabel("Paciente Activo: Cargando...");
        lblPaciente.setForeground(Color.WHITE);
        lblPaciente.setFont(new Font("Arial", Font.BOLD, 16));
        banner.add(lblPaciente);
        return banner;
    }

    private JPanel construirPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblMedicamento = new JLabel("Próxima toma: Ninguna pendiente");
        lblHora = new JLabel("Hora: --:--");
        lblDosis = new JLabel("Dosis: --");
        lblAdherencia = new JLabel("Adherencia General: 100.0%");
        lblAdherencia.setFont(new Font("Arial", Font.BOLD, 14));
        lblAdherencia.setForeground(new Color(41, 128, 185));

        btnTomar = new JButton("Registrar Toma Normal");
        btnManual = new JButton("Toma Manual (Olvidada)");
        btnSaltar = new JButton("Omitir Toma");

        btnTomar.setBackground(new Color(46, 204, 113)); btnTomar.setForeground(Color.WHITE);
        btnManual.setBackground(new Color(243, 156, 18)); btnManual.setForeground(Color.WHITE);
        btnSaltar.setBackground(new Color(231, 76, 60)); btnSaltar.setForeground(Color.WHITE);

        panel.add(lblMedicamento);
        panel.add(lblHora);
        panel.add(lblDosis);
        panel.add(Box.createVerticalStrut(25));
        
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBotones.add(btnTomar);
        pnlBotones.add(btnManual);
        pnlBotones.add(btnSaltar);
        panel.add(pnlBotones);
        
        panel.add(Box.createVerticalStrut(25));
        panel.add(lblAdherencia);

        return panel;
    }

    private void configurarBotones() {
        btnTomar.addActionListener(e -> {
            if (tratamientoActual != null) {
                if (!tratamientoActual.verificarAlertaHorario()) {
                    JOptionPane.showMessageDialog(this, "Aún no es hora de tomar este medicamento.", "Bloqueo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                agendaCtrl.registrarToma(tratamientoActual);
                JOptionPane.showMessageDialog(this, "Toma registrada a tiempo.");
                actualizarVista();
            }
        });

        btnManual.addActionListener(e -> {
            if (tratamientoActual != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Registrar toma atrasada con la hora actual?", "Toma Manual", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    agendaCtrl.registrarToma(tratamientoActual, LocalDateTime.now());
                    JOptionPane.showMessageDialog(this, "Toma manual guardada.");
                    actualizarVista();
                }
            }
        });

        btnSaltar.addActionListener(e -> {
            if (tratamientoActual != null) {
                agendaCtrl.omitirToma(tratamientoActual);
                JOptionPane.showMessageDialog(this, "Toma omitida.");
                actualizarVista();
            }
        });
    }

    public void actualizarVista() {
        if (pacienteCtrl == null || pacienteCtrl.getPacienteActual() == null) return;
        
        lblPaciente.setText("Paciente Activo: " + pacienteCtrl.getPacienteActual().getNombre());
        double adh = pacienteCtrl.obtenerReporteAdherencia();
        lblAdherencia.setText(String.format("Adherencia general: %.1f%%", adh));

        List<Tratamiento> lista = pacienteCtrl.getPacienteActual().getTratamientos();
        Tratamiento proximo = null;
        for (Tratamiento t : lista) {
            if (proximo == null || t.getProximaToma().isBefore(proximo.getProximaToma())) {
                proximo = t;
            }
        }
        this.tratamientoActual = proximo;

        if (proximo != null) {
            lblMedicamento.setText("Próxima toma: " + proximo.getMedicina().getNombre() + " (" + proximo.getMedicina().getPrincipioActivo() + ")");
            lblHora.setText("Hora: " + proximo.getProximaToma().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            lblDosis.setText("Dosis: " + proximo.getDosis() + " " + proximo.getMedicina().getUnidadMedida());
            btnTomar.setEnabled(true); btnManual.setEnabled(true); btnSaltar.setEnabled(true);
        } else {
            lblMedicamento.setText("Próxima toma: Ninguna agendada");
            lblHora.setText("Hora: —"); lblDosis.setText("Dosis: —");
            btnTomar.setEnabled(false); btnManual.setEnabled(false); btnSaltar.setEnabled(false);
        }
    }

    public void actualizarControladores(PacienteController pCtrl, AgendaController aCtrl) {
        this.pacienteCtrl = pCtrl;
        this.agendaCtrl = aCtrl;
        actualizarVista();
    }
}
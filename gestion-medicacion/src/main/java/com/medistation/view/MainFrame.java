package com.medistation.view;

import javax.swing.*;
import java.awt.*;
import com.medistation.controller.AgendaController;
import com.medistation.controller.InventarioController;
import com.medistation.controller.PacienteController;
import com.medistation.strategy.DetectorSobredosis;
import com.medistation.strategy.DetectorSomnolencia;

// ventana principal y hub de navegacion
public class MainFrame extends JFrame {

    private PacienteController  pacienteCtrl;
    private InventarioController inventarioCtrl;
    private AgendaController    agendaCtrl;
    private DetectorSobredosis  detector;

    private JLabel lblPacienteActivo;

    private DashboardFrame  dashFrame;
    private InventarioFrame invFrame;
    private AgendaFrame     agendaFrame;
    private HistorialFrame  histFrame;
    private PacienteFrame   pacienteFrame;

    public MainFrame(PacienteController pCtrl, InventarioController iCtrl) {
        this.pacienteCtrl   = pCtrl;
        this.inventarioCtrl = iCtrl;
        this.detector       = new DetectorSobredosis();
        this.agendaCtrl = null; // se define al elegir paciente

        setTitle("MediStation — Gestor de Medicación Inteligente");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        construirUI();
    }

    private void construirUI() {
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        JLabel titulo = new JLabel("MediStation");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);
        add(header, BorderLayout.NORTH);

        JPanel menuGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        menuGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        menuGrid.add(crearBoton("👤  Pacientes",    new Color(155, 89, 182), e -> abrirPacientes()));
        menuGrid.add(crearBoton("💊  Inventario",   new Color(52, 152, 219),  e -> abrirInventario()));
        menuGrid.add(crearBoton("📅  Agenda",       new Color(39, 174, 96),   e -> abrirAgenda()));
        menuGrid.add(crearBoton("🏠  Dashboard",    new Color(230, 126, 34),  e -> abrirDashboard()));
        menuGrid.add(crearBoton("📋  Historial",    new Color(149, 165, 166), e -> abrirHistorial()));
        menuGrid.add(new JLabel()); 

        add(menuGrid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(44, 62, 80));
        lblPacienteActivo = new JLabel("Paciente activo: ninguno — abra 'Pacientes' para comenzar");
        lblPacienteActivo.setForeground(Color.WHITE);
        lblPacienteActivo.setFont(new Font("Arial", Font.BOLD, 12));
        footer.add(lblPacienteActivo);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Color fondo, java.awt.event.ActionListener accion) {
        JButton btn = new JButton("<html><center>" + texto + "</center></html>");
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 70));
        btn.addActionListener(accion);
        return btn;
    }

    private void abrirPacientes() {
        if (pacienteFrame == null || !pacienteFrame.isDisplayable()) {
            pacienteFrame = new PacienteFrame(pacienteCtrl, this);
        }
        pacienteFrame.actualizarTabla();
        pacienteFrame.setVisible(true);
        pacienteFrame.toFront();
    }

    private void abrirInventario() {
        if (invFrame == null || !invFrame.isDisplayable()) {
            invFrame = new InventarioFrame(inventarioCtrl, detector);
        }
        invFrame.actualizarTabla();
        invFrame.setVisible(true);
        invFrame.toFront();
    }

    private void abrirAgenda() {
        if (agendaCtrl == null) {
            JOptionPane.showMessageDialog(this, "Primero seleccione un paciente en 'Pacientes'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (agendaFrame == null || !agendaFrame.isDisplayable()) {
            agendaFrame = new AgendaFrame(pacienteCtrl, agendaCtrl, inventarioCtrl);
        }
        agendaFrame.refrescarCombos();
        agendaFrame.actualizarListado();
        agendaFrame.setVisible(true);
        agendaFrame.toFront();
    }

    private void abrirDashboard() {
        if (agendaCtrl == null) {
            JOptionPane.showMessageDialog(this, "Primero seleccione un paciente en 'Pacientes'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dashFrame == null || !dashFrame.isDisplayable()) {
            dashFrame = new DashboardFrame(pacienteCtrl, agendaCtrl);
        }
        dashFrame.actualizarVista();
        dashFrame.setVisible(true);
        dashFrame.toFront();
    }

    private void abrirHistorial() {
        if (histFrame == null || !histFrame.isDisplayable()) {
            histFrame = new HistorialFrame(pacienteCtrl);
        }
        histFrame.cargarHistorial();
        histFrame.setVisible(true);
        histFrame.toFront();
    }

    // refresca controladores de ventanas hijas al cambiar usuario
    public void onPacienteSeleccionado() {
        agendaCtrl = new AgendaController(pacienteCtrl.getPacienteActual());
        agendaCtrl.agregarReglaValidacion(new DetectorSobredosis());
        agendaCtrl.agregarReglaValidacion(new DetectorSomnolencia());

        lblPacienteActivo.setText("Paciente activo: " + pacienteCtrl.getPacienteActual().getNombre());

        if (dashFrame != null && dashFrame.isDisplayable()) dashFrame.actualizarControladores(pacienteCtrl, agendaCtrl);
        if (agendaFrame != null && agendaFrame.isDisplayable()) agendaFrame.actualizarControladores(agendaCtrl);
        if (histFrame != null && histFrame.isDisplayable()) histFrame.cargarHistorial();
    }

    // limpia interfaz si se borra el paciente activo
    public void onPacienteEliminado() {
        this.agendaCtrl = null;
        lblPacienteActivo.setText("Paciente activo: ninguno — abra 'Pacientes' para comenzar");
        if (dashFrame != null && dashFrame.isDisplayable()) dashFrame.dispose();
        if (agendaFrame != null && agendaFrame.isDisplayable()) agendaFrame.dispose();
        if (histFrame != null && histFrame.isDisplayable()) histFrame.dispose();
    }
}
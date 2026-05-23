package com.medistation.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import com.medistation.controller.AgendaController;
import com.medistation.controller.InventarioController;
import com.medistation.controller.PacienteController;
import com.medistation.model.Medicamento;
import com.medistation.model.Tratamiento;

// ventana para agendar tratamientos
public class AgendaFrame extends JFrame {

    private PacienteController pacienteCtrl;
    private AgendaController agendaCtrl;
    private InventarioController inventarioCtrl;

    private JLabel lblPacienteActivo;
    private JComboBox<String> cbMedicamentos;
    private JTextField txtFrecuencia;
    private JTextField txtDosis;
    private DefaultListModel<String> modeloLista;

    public AgendaFrame(PacienteController pacienteCtrl, AgendaController agendaCtrl, InventarioController inventarioCtrl) {
        this.pacienteCtrl = pacienteCtrl;
        this.agendaCtrl = agendaCtrl;
        this.inventarioCtrl = inventarioCtrl;

        setTitle("MediStation — Agenda de Tratamientos");
        setSize(620, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(construirBanner(), BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.WEST);
        add(construirPanelLista(), BorderLayout.CENTER);

        refrescarCombos();
        actualizarListado();
    }

    private JPanel construirBanner() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setBackground(new Color(39, 174, 96));
        lblPacienteActivo = new JLabel();
        lblPacienteActivo.setForeground(Color.WHITE);
        lblPacienteActivo.setFont(new Font("Arial", Font.BOLD, 14));
        actualizarBanner();
        banner.add(lblPacienteActivo);
        return banner;
    }

    private void actualizarBanner() {
        if (pacienteCtrl != null && pacienteCtrl.getPacienteActual() != null) {
            lblPacienteActivo.setText("Agendando a: " + pacienteCtrl.getPacienteActual().getNombre());
        } else {
            lblPacienteActivo.setText("Agendando a: Ninguno");
        }
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cbMedicamentos = new JComboBox<>();
        txtFrecuencia = new JTextField();
        txtDosis = new JTextField();
        JButton btnAgendar = new JButton("Agendar Tratamiento");

        panel.add(new JLabel("Medicamento:"));
        panel.add(cbMedicamentos);
        panel.add(new JLabel("Frecuencia (horas):"));
        panel.add(txtFrecuencia);
        panel.add(new JLabel("Dosis por Toma:"));
        panel.add(txtDosis);
        panel.add(new JLabel());
        panel.add(btnAgendar);

        btnAgendar.addActionListener(e -> ejecutarAgendamiento());
        return panel;
    }

    private JPanel construirPanelLista() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tratamientos Activos"));
        modeloLista = new DefaultListModel<>();
        JList<String> lista = new JList<>(modeloLista);
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        return panel;
    }

    public void refrescarCombos() {
        cbMedicamentos.removeAllItems();
        for (Medicamento m : inventarioCtrl.getCatalogoMedicamentos()) {
            cbMedicamentos.addItem(m.getNombre());
        }
    }

    public void actualizarListado() {
        modeloLista.clear();
        if (pacienteCtrl.getPacienteActual() != null) {
            for (Tratamiento t : pacienteCtrl.getPacienteActual().getTratamientos()) {
                modeloLista.addElement(t.getMedicina().getNombre() + " - Cada " + t.getFrecuenciaHoras() + " hrs (Dosis: " + t.getDosis() + ")");
            }
        }
    }

    private void ejecutarAgendamiento() {
        if (cbMedicamentos.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar fármacos primero.");
            return;
        }
        try {
            String nombreMed = (String) cbMedicamentos.getSelectedItem();
            Medicamento med = inventarioCtrl.buscarMedicamento(nombreMed);
            int freq = Integer.parseInt(txtFrecuencia.getText().trim());
            double dosis = Double.parseDouble(txtDosis.getText().trim());

            if (freq <= 0 || dosis <= 0) {
                throw new IllegalArgumentException("La frecuencia y la dosis deben ser mayores a cero.");
            }

            Tratamiento nuevo = new Tratamiento(pacienteCtrl.getPacienteActual(), med, freq, dosis, LocalDateTime.now());
            boolean exito = agendaCtrl.intentarAgendarTratamiento(nuevo);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Tratamiento agendado para " + pacienteCtrl.getPacienteActual().getNombre() + ".");
                actualizarListado();
                txtFrecuencia.setText(""); 
                txtDosis.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "RECHAZADO: conflicto médico detectado (sobredosis o somnolencia cruzada).", "Alerta médica", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Frecuencia y dosis deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarControladores(AgendaController nuevoAgenda) {
        this.agendaCtrl = nuevoAgenda;
        actualizarBanner();
        actualizarListado();
    }
}
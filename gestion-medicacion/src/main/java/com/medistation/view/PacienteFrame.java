package com.medistation.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.medistation.controller.PacienteController;
import com.medistation.model.Paciente;
import com.medistation.model.TipoDiscapacidad;

// permite crear, seleccionar y borrar pacientes
public class PacienteFrame extends JFrame {

    private final PacienteController pacienteCtrl;
    private final MainFrame mainFrame;

    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre;
    private JTextField txtEdad;
    private JTextField txtPeso;
    private JTextField txtAlergia;
    private JComboBox<TipoDiscapacidad> cbDiscapacidad;

    public PacienteFrame(PacienteController pacienteCtrl, MainFrame mainFrame) {
        this.pacienteCtrl = pacienteCtrl;
        this.mainFrame    = mainFrame;

        setTitle("MediStation — Gestión de Pacientes");
        setSize(860, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout(10, 10));

        add(construirPanelFormulario(), BorderLayout.WEST);
        add(construirPanelTabla(), BorderLayout.CENTER);
        
        actualizarTabla();
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo Paciente"));

        txtNombre = new JTextField();
        txtEdad = new JTextField();
        txtPeso = new JTextField();
        txtAlergia = new JTextField();
        cbDiscapacidad = new JComboBox<>(TipoDiscapacidad.values());

        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Edad:")); panel.add(txtEdad);
        panel.add(new JLabel("Peso (kg):")); panel.add(txtPeso);
        panel.add(new JLabel("Alergia principal:")); panel.add(txtAlergia);
        panel.add(new JLabel("Discapacidad:")); panel.add(cbDiscapacidad);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> ejecutarRegistro());
        panel.add(new JLabel());
        panel.add(btnRegistrar);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"Nombre", "Edad", "Peso", "Alergias", "Discapacidades"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPacientes = new JTable(modeloTabla);
        panel.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSeleccionar = new JButton("Usar Perfil Seleccionado");
        JButton btnEliminar = new JButton("Eliminar Paciente");

        btnSeleccionar.addActionListener(e -> ejecutarSeleccion());
        btnEliminar.addActionListener(e -> ejecutarEliminacion());

        panelSur.add(btnSeleccionar);
        panelSur.add(btnEliminar);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Paciente p : pacienteCtrl.getPacientesRegistrados()) {
            String alergias = p.getPerfil().getAlergias().isEmpty() ? "Ninguna" : String.join(", ", p.getPerfil().getAlergias());
            StringBuilder disc = new StringBuilder();
            
            for (TipoDiscapacidad d : p.getPerfil().getDiscapacidades()) {
                if (disc.length() > 0) disc.append(", ");
                disc.append(d.name());
            }
            if (disc.length() == 0) disc.append("NINGUNA");

            modeloTabla.addRow(new Object[]{ p.getNombre(), p.getEdad(), p.getPerfil().getPeso(), alergias, disc.toString() });
        }
    }

    private void ejecutarRegistro() {
        try {
            String nombre = txtNombre.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());
            String alergia = txtAlergia.getText().trim();
            TipoDiscapacidad disc = (TipoDiscapacidad) cbDiscapacidad.getSelectedItem();

            pacienteCtrl.registrarPaciente(nombre, edad, peso);
            pacienteCtrl.seleccionarPaciente(nombre);
            
            if (!alergia.isEmpty()) pacienteCtrl.agregarAlergia(alergia);
            if (disc != TipoDiscapacidad.NINGUNA) pacienteCtrl.activarDiscapacidad(disc);

            actualizarTabla();
            txtNombre.setText(""); txtEdad.setText(""); txtPeso.setText(""); txtAlergia.setText("");
            cbDiscapacidad.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this, "Paciente registrado y activo.");
            mainFrame.onPacienteSeleccionado();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Edad y Peso deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarSeleccion() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.");
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        pacienteCtrl.seleccionarPaciente(nombre);
        mainFrame.onPacienteSeleccionado();
        JOptionPane.showMessageDialog(this, "Sesión cambiada a: " + nombre);
    }

    private void ejecutarEliminacion() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.");
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar a " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (pacienteCtrl.eliminarPaciente(nombre)) {
                actualizarTabla();
                mainFrame.onPacienteEliminado();
                JOptionPane.showMessageDialog(this, "Paciente eliminado.");
            }
        }
    }
}
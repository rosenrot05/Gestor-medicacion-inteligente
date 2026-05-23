package com.medistation.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.medistation.controller.InventarioController;
import com.medistation.model.Medicamento;
import com.medistation.strategy.DetectorSobredosis;

// gestiona el catalogo de medicamentos
public class InventarioFrame extends JFrame {

    private final InventarioController inventarioCtrl;
    private final DetectorSobredosis detector;

    private JTable tablaMedicamentos;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre;
    private JTextField txtPrincipioActivo; 
    private JTextField txtEnvases;
    private JTextField txtContenido;
    private JTextField txtUmbral;
    private JComboBox<String> cbTipo;

    public InventarioFrame(InventarioController inventarioCtrl, DetectorSobredosis detector) {
        this.inventarioCtrl = inventarioCtrl;
        this.detector = detector;

        setTitle("MediStation — Inventario de Fármacos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        add(construirPanelAcciones(), BorderLayout.SOUTH);
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Fármaco"));

        txtNombre = new JTextField();
        txtPrincipioActivo = new JTextField();
        txtEnvases = new JTextField();
        txtContenido = new JTextField();
        txtUmbral = new JTextField();
        cbTipo = new JComboBox<>(new String[]{"Cápsula", "Jarabe", "Inhalador"});

        // bloquea el umbral para liquidos
        cbTipo.addActionListener(e -> {
            if (cbTipo.getSelectedItem().equals("Cápsula")) {
                txtUmbral.setEnabled(true);
                txtUmbral.setText("");
            } else {
                txtUmbral.setEnabled(false);
                txtUmbral.setText("Automático");
            }
        });

        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Principio Activo:")); panel.add(txtPrincipioActivo);
        panel.add(new JLabel("Tipo:")); panel.add(cbTipo);
        panel.add(new JLabel("Tarros/Envases:")); panel.add(txtEnvases);
        panel.add(new JLabel("Contenido p/Tarro:")); panel.add(txtContenido);
        panel.add(new JLabel("Umbral (Cápsulas):")); panel.add(txtUmbral);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> ejecutarRegistro());
        panel.add(new JLabel());
        panel.add(btnGuardar);

        return panel;
    }

    private JPanel construirTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"Nombre", "Stock Total", "Tarros", "Unidad", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaMedicamentos = new JTable(modeloTabla);
        panel.add(new JScrollPane(tablaMedicamentos), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JButton btnReabastecer = new JButton("Reabastecer Envases");
        JButton btnEliminar = new JButton("Eliminar Fármaco");
        
        btnReabastecer.addActionListener(e -> ejecutarReabastecimiento());
        btnEliminar.addActionListener(e -> ejecutarEliminacion());
        
        panel.add(btnReabastecer);
        panel.add(btnEliminar);
        return panel;
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Medicamento m : inventarioCtrl.getCatalogoMedicamentos()) {
            String estado = m.alertarBajoStock() ? "⚠️ BAJO STOCK" : "OK";
            modeloTabla.addRow(new Object[]{
                m.getNombre(), m.getStock(), m.getCantidadEnvases(), m.getUnidadMedida(), estado
            });
        }
    }

    private void ejecutarRegistro() {
        try {
            String nombre = txtNombre.getText().trim();
            String principio = txtPrincipioActivo.getText().trim();
            int envases = Integer.parseInt(txtEnvases.getText().trim());
            double contenido = Double.parseDouble(txtContenido.getText().trim());
            String tipo = (String) cbTipo.getSelectedItem();

            if (nombre.isEmpty() || principio.isEmpty()) {
                throw new IllegalArgumentException("Nombre y principio activo son obligatorios.");
            }

            if (tipo.equals("Cápsula")) {
                double umbral = Double.parseDouble(txtUmbral.getText().trim());
                inventarioCtrl.registrarCapsula(nombre, principio, envases, contenido, umbral);
            } else if (tipo.equals("Jarabe")) {
                inventarioCtrl.registrarJarabe(nombre, principio, envases, contenido);
            } else {
                inventarioCtrl.registrarInhalador(nombre, principio, envases, contenido);
            }

            actualizarTabla();
            JOptionPane.showMessageDialog(this, "Registrado exitosamente.");
            txtNombre.setText(""); txtPrincipioActivo.setText("");
            txtEnvases.setText(""); txtContenido.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNombreSeleccionado() {
        int fila = tablaMedicamentos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un fármaco en la tabla.");
            return null;
        }
        return (String) modeloTabla.getValueAt(fila, 0);
    }

    private void ejecutarReabastecimiento() {
        String nombre = getNombreSeleccionado();
        if (nombre == null) return;

        String input = JOptionPane.showInputDialog(this, "Nuevos envases para " + nombre + ":");
        if (input == null || input.trim().isEmpty()) return;

        try {
            inventarioCtrl.reabastecerMedicamento(nombre, Integer.parseInt(input.trim()));
            actualizarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarEliminacion() {
        String nombre = getNombreSeleccionado();
        if (nombre == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (inventarioCtrl.eliminarMedicamento(nombre)) {
                actualizarTabla();
            }
        }
    }
}
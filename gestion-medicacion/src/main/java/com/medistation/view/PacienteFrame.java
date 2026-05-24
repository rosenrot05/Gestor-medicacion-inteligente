package com.medistation.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.medistation.controller.PacienteController;
import com.medistation.model.GestorArchivosTXT;
import com.medistation.model.Paciente;
import com.medistation.model.TipoDiscapacidad;

// permite crear, seleccionar, actualizar y borrar pacientes
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));

        txtNombre = new JTextField();
        txtEdad = new JTextField();
        txtPeso = new JTextField();
        txtAlergia = new JTextField();
        cbDiscapacidad = new JComboBox<>(TipoDiscapacidad.values());

        panel.add(new JLabel("Nombre (Identificador):")); panel.add(txtNombre);
        panel.add(new JLabel("Edad:")); panel.add(txtEdad);
        panel.add(new JLabel("Peso (kg):")); panel.add(txtPeso);
        panel.add(new JLabel("Alergia principal:")); panel.add(txtAlergia);
        panel.add(new JLabel("Discapacidad:")); panel.add(cbDiscapacidad);

        JButton btnLimpiar = new JButton("Limpiar / Nuevo");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnRegistrar = new JButton("Registrar / Actualizar");
        btnRegistrar.addActionListener(e -> ejecutarRegistro());

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 0));
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrar);

        panel.add(new JLabel()); // Espacio vacío para alinear
        panel.add(panelBotones);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"Nombre", "Edad", "Peso", "Alergias", "Discapacidades"};
        
        //evitar que el usuario edite la tabla directamente haciendo doble clic
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaPacientes = new JTable(modeloTabla);
        
        //al hacer clic en una fila, los datos suben al formulario
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaPacientes.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombre.setEnabled(false); // Deshabilitamos nombre porque actúa como ID
                    txtEdad.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtPeso.setText(modeloTabla.getValueAt(fila, 2).toString());
                    
                    String alergias = modeloTabla.getValueAt(fila, 3).toString();
                    txtAlergia.setText(alergias.equals("NINGUNA") ? "" : alergias);
                }
            }
        });

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
            String alergias = p.getPerfil().getAlergias().isEmpty() ? "NINGUNA" : String.join(", ", p.getPerfil().getAlergias());
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
            if(nombre.isEmpty()) throw new Exception("El nombre no puede estar vacío.");
            
            int edad = Integer.parseInt(txtEdad.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());
            String alergia = txtAlergia.getText().trim();
            TipoDiscapacidad disc = (TipoDiscapacidad) cbDiscapacidad.getSelectedItem();

            // CORRECCIÓN 3: Revisar si el paciente ya existe para Actualizar en vez de Crear
            boolean existe = false;
            for (Paciente p : pacienteCtrl.getPacientesRegistrados()) {
                if (p.getNombre().equalsIgnoreCase(nombre)) {
                    existe = true;
                    // Actualizamos sus datos (Asegúrate de tener los métodos setEdad y setPeso en tu modelo)
                    p.setEdad(edad);
                    if (p.getPerfil() != null) {
                        p.getPerfil().setPeso(peso);
                    }
                    
                    pacienteCtrl.seleccionarPaciente(nombre);
                    if (!alergia.isEmpty() && !p.getPerfil().getAlergias().contains(alergia)) {
                        pacienteCtrl.agregarAlergia(alergia);
                    }
                    if (!p.getPerfil().getDiscapacidades().contains(disc)) {
                        pacienteCtrl.activarDiscapacidad(disc);
                    }
                    break;
                }
            }

            if (!existe) {
                //si no existía, lo registramos como nuevo
                pacienteCtrl.registrarPaciente(nombre, edad, peso);
                pacienteCtrl.seleccionarPaciente(nombre);
                if (!alergia.isEmpty()) {
                    pacienteCtrl.agregarAlergia(alergia);
                }
                pacienteCtrl.activarDiscapacidad(disc);
            }

            // Guardamos cambios en TXT
            GestorArchivosTXT.guardarDatos(pacienteCtrl, null);

            actualizarTabla();
            limpiarFormulario();
            
            JOptionPane.showMessageDialog(this, existe ? "Paciente actualizado correctamente." : "Nuevo paciente registrado correctamente.");
            mainFrame.onPacienteSeleccionado();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La Edad y el Peso deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); 
        txtNombre.setEnabled(true); //volver a habilitar para nuevos pacientes
        txtEdad.setText(""); 
        txtPeso.setText("");
        txtAlergia.setText(""); 
        cbDiscapacidad.setSelectedIndex(0);
        tablaPacientes.clearSelection();
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
                GestorArchivosTXT.guardarDatos(pacienteCtrl, null); 
                actualizarTabla();
                limpiarFormulario();
                mainFrame.onPacienteEliminado();
                JOptionPane.showMessageDialog(this, "Paciente eliminado.");
            }
        }
    }
}

package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;
import facitec.edu.py.pretasystem_util.FormatoUtil;

public class MateriaPrimaVentana extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombre;
    private JSpinner spnStockActual;
    private JSpinner spnStockMinimo;
    private JComboBox<String> cmbUnidad;
    private JTextField txtBuscar;
    private JTable table;
    private DefaultTableModel modeloTabla;

    private final Materia_Prima_Dao dao = new Materia_Prima_Dao();

    // Guarda el registro seleccionado de la tabla.
    private Materia_Prima materiaSeleccionada = null;

    // Indica si el formulario está trabajando en modo edición.
    private boolean modoEdicion = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MateriaPrimaVentana frame = new MateriaPrimaVentana();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MateriaPrimaVentana() {

        setTitle("ABM - Materia Prima");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1024, 680);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // ---------- PANEL FORMULARIO ----------

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLACK));
        panel.setBounds(10, 10, 278, 623);
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(10, 10, 100, 14);
        panel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(10, 32, 262, 22);
        panel.add(txtNombre);

        JLabel lblStockActual = new JLabel("Stock Actual:");
        lblStockActual.setBounds(10, 65, 100, 14);
        panel.add(lblStockActual);

        spnStockActual = new JSpinner(
                new SpinnerNumberModel(0, 0, 999999, 1));
        FormatoUtil.aplicarMiles(spnStockActual);
        spnStockActual.setBounds(10, 85, 163, 24);
        panel.add(spnStockActual);

        cmbUnidad = new JComboBox<>(
                new String[] { "Kg", "g", "L", "ml", "Unid." });
        cmbUnidad.setBounds(185, 85, 87, 24);
        panel.add(cmbUnidad);

        JLabel lblStockMinimo = new JLabel("Stock Mínimo:");
        lblStockMinimo.setBounds(10, 122, 100, 14);
        panel.add(lblStockMinimo);

        spnStockMinimo = new JSpinner(
                new SpinnerNumberModel(0, 0, 999999, 1));
        FormatoUtil.aplicarMiles(spnStockMinimo);
        spnStockMinimo.setBounds(10, 144, 262, 27);
        panel.add(spnStockMinimo);

        JSeparator separator = new JSeparator();
        separator.setBounds(10, 480, 262, 6);
        panel.add(separator);

        // ---------- BOTÓN GUARDAR ----------

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(10, 514, 115, 32);
        btnGuardar.addActionListener(e -> guardar());
        panel.add(btnGuardar);

        // ---------- BOTÓN EDITAR ----------

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(154, 514, 115, 32);
        btnEditar.addActionListener(e -> editar());
        panel.add(btnEditar);

        // ---------- BOTÓN ELIMINAR ----------

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(10, 554, 115, 32);
        btnEliminar.addActionListener(e -> eliminar());
        panel.add(btnEliminar);

        // ---------- BOTÓN SALIR ----------

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBounds(154, 554, 115, 32);
        btnSalir.addActionListener(e -> salir());
        panel.add(btnSalir);

        // ---------- BOTÓN CANCELAR ----------

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(84, 590, 115, 27);
        btnCancelar.addActionListener(e -> limpiar());
        panel.add(btnCancelar);

        // ---------- BUSCADOR ----------

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setBounds(311, 18, 50, 22);
        getContentPane().add(lblBuscar);

        // Icono de lupa.
        JLabel lblLupa = new JLabel("\uD83D\uDD0D");
        lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblLupa.setBounds(365, 15, 25, 25);
        getContentPane().add(lblLupa);

        txtBuscar = new JTextField();
        txtBuscar.setBounds(393, 18, 272, 22);
        getContentPane().add(txtBuscar);

        // Actualiza la tabla cuando cambia el texto del buscador.
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarTabla();
            }
        });

        // ---------- TABLA ----------

        modeloTabla = new DefaultTableModel(
                new Object[] {
                        "ID",
                        "Nombre",
                        "Stock Actual",
                        "Unidad",
                        "Stock Mínimo"
                }, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(modeloTabla);

        table.getColumnModel().getColumn(2)
                .setCellRenderer(FormatoUtil.rendererMiles());

        table.getColumnModel().getColumn(4)
                .setCellRenderer(FormatoUtil.rendererMiles());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Al seleccionar una fila, solamente recupera el registro.
        // Los datos no se cargan en el formulario todavía.
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRegistro();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(311, 55, 676, 578);
        getContentPane().add(scroll);

        cargarTabla();
    }

    // ====================== LÓGICA ======================

    /**
     * Carga las materias primas en la tabla.
     * Utiliza el filtro escrito en el buscador.
     */
    private void cargarTabla() {

        String filtro = txtBuscar == null
                ? ""
                : txtBuscar.getText().trim();

        List<Materia_Prima> lista = dao.recuperarPorFiltro(filtro);

        modeloTabla.setRowCount(0);

        for (Materia_Prima m : lista) {

            modeloTabla.addRow(new Object[] {
                    m.getId_materia_prima(),
                    m.getNombre(),
                    m.getStock_actual(),
                    m.getUnidad_medida(),
                    m.getStock_minimo()
            });
        }
    }

    /**
     * Recupera el registro seleccionado en la tabla,
     * pero no carga sus datos en el formulario.
     */
    private void seleccionarRegistro() {

        int fila = table.getSelectedRow();

        if (fila < 0) {
            materiaSeleccionada = null;
            modoEdicion = false;
            return;
        }

        // Obtiene el ID de la fila seleccionada.
        Integer id = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();

        // Recupera la materia prima desde la base de datos.
        materiaSeleccionada = dao.recuperarPorId(id);

        // Todavía no se permite guardar como edición.
        modoEdicion = false;
    }

    /**
     * Registra una nueva materia prima o actualiza
     * el registro seleccionado cuando está en modo edición.
     */
    private void guardar() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        try {

            if (modoEdicion) {

                // Comprueba que exista un registro para modificar.
                if (materiaSeleccionada == null) {
                    JOptionPane.showMessageDialog(
                            this, "Seleccione una materia prima para editar");
                    return;
                }

                // Actualiza los datos del registro existente.
                materiaSeleccionada.setNombre(nombre);

                materiaSeleccionada.setStock_actual(
                        ((Number) spnStockActual.getValue()).intValue());

                materiaSeleccionada.setUnidad_medida(
                        cmbUnidad.getSelectedItem().toString());

                materiaSeleccionada.setStock_minimo(
                        ((Number) spnStockMinimo.getValue()).intValue());

                // Guarda los cambios en la base de datos.
                dao.guardar(materiaSeleccionada);

                JOptionPane.showMessageDialog(
                        this, "Materia prima editada correctamente");

            } else {

                // Crea un nuevo registro.
                Materia_Prima m = new Materia_Prima();

                m.setNombre(nombre);

                m.setStock_actual(
                        ((Number) spnStockActual.getValue()).intValue());

                m.setUnidad_medida(
                        cmbUnidad.getSelectedItem().toString());

                m.setStock_minimo(
                        ((Number) spnStockMinimo.getValue()).intValue());

                // Guarda la nueva materia prima.
                dao.guardar(m);

                JOptionPane.showMessageDialog(
                        this, "Materia prima guardada correctamente");
            }

            // Limpia el formulario y actualiza la tabla.
            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this, "Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Activa el modo edición y carga los datos
     * de la materia prima seleccionada en el formulario.
     */
    private void editar() {

        if (materiaSeleccionada == null) {
            JOptionPane.showMessageDialog(
                    this, "Seleccione una materia prima de la tabla");
            return;
        }

        // Carga los datos del registro seleccionado.
        txtNombre.setText(materiaSeleccionada.getNombre());

        spnStockActual.setValue(
                materiaSeleccionada.getStock_actual());

        cmbUnidad.setSelectedItem(
                materiaSeleccionada.getUnidad_medida());

        spnStockMinimo.setValue(
                materiaSeleccionada.getStock_minimo());

        // Activa el modo edición.
        modoEdicion = true;

        JOptionPane.showMessageDialog(
                this, "Puede modificar los datos del registro seleccionado");
    }

    /**
     * Elimina la materia prima seleccionada.
     */
    private void eliminar() {

        if (materiaSeleccionada == null) {
            JOptionPane.showMessageDialog(
                    this, "Seleccione un registro de la tabla");
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar \"" + materiaSeleccionada.getNombre() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            dao.eliminar(materiaSeleccionada);

            JOptionPane.showMessageDialog(
                    this, "Materia prima eliminada correctamente");

            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this, "Error al eliminar: " + ex.getMessage());
        }
    }

    /**
     * Limpia el formulario, elimina la selección
     * y desactiva el modo edición.
     */
    private void limpiar() {

        materiaSeleccionada = null;
        modoEdicion = false;

        txtNombre.setText("");
        spnStockActual.setValue(0);
        spnStockMinimo.setValue(0);
        cmbUnidad.setSelectedIndex(0);

        table.clearSelection();

        txtNombre.requestFocus();
    }

    /**
     * Cierra solamente la ventana de materias primas.
     */
    private void salir() {

        dispose();
    }
}


package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_dao.Producto_Terminado_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_entidades.Producto_Terminado;
import facitec.edu.py.pretasystem_util.FormatoUtil;

public class ProductoTerminadoVentana extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombre;
    private JTextField txtColor;

    private JComboBox<Categoria_Producto> cmbCategoria;
    private JComboBox<Orden_Produccion> cmbOrden;

    private JLabel lblInfoOrden;
    private JLabel lblStockActual;

    private JSpinner spnProducido;
    private JSpinner spnFecha;
    private JSpinner spnStockActual;
    private JSpinner spnStockMinimo;
    private JSpinner spnPrecioMinorista;
    private JSpinner spnPrecioMayorista;

    private JTextField txtBuscar;

    private JTable table;
    private DefaultTableModel modelo;

    private final Producto_Terminado_Dao dao = new Producto_Terminado_Dao();
    private final Categoria_Producto_Dao categoriaDao = new Categoria_Producto_Dao();
    private final Orden_Produccion_Dao ordenDao = new Orden_Produccion_Dao();

    private Producto_Terminado seleccionado = null;

    private boolean modoEdicion = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new ProductoTerminadoVentana().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ProductoTerminadoVentana() {

        setTitle("ABM - Producto Terminado");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1250, 680);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // ---------- FORMULARIO ----------

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLACK));
        panel.setBounds(10, 10, 300, 623);
        panel.setLayout(null);
        getContentPane().add(panel);

        txtNombre = campoTexto(panel, "Nombre:", 10);
        txtColor = campoTexto(panel, "Color:", 65);

        JLabel lblCat = new JLabel("Categoria:");
        lblCat.setBounds(10, 120, 200, 14);
        panel.add(lblCat);

        cmbCategoria = new JComboBox<>();
        cmbCategoria.setBounds(10, 140, 280, 24);

        cmbCategoria.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {

                super.getListCellRendererComponent(l, v, i, s, f);

                setText(v instanceof Categoria_Producto
                        ? ((Categoria_Producto) v).getNombre_categoria()
                        : "");

                return this;
            }
        });

        panel.add(cmbCategoria);

        // ---------- ORDEN DE PRODUCCION ----------

        JLabel lblOrden = new JLabel("Orden de produccion:");
        lblOrden.setBounds(10, 175, 200, 14);
        panel.add(lblOrden);

        cmbOrden = new JComboBox<>();
        cmbOrden.setBounds(10, 195, 280, 24);

        cmbOrden.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {

                super.getListCellRendererComponent(l, v, i, s, f);

                if (v instanceof Orden_Produccion) {
                    Orden_Produccion o = (Orden_Produccion) v;

                    setText("Orden N° " + o.getId_orden_produccion()
                            + " - " + o.getEtapa_actual()
                            + " - pendiente: " + FormatoUtil.miles(o.getPendiente()));

                } else {
                    setText("(Sin orden)");
                }

                return this;
            }
        });

        cmbOrden.addActionListener(e -> actualizarInfoOrden());
        panel.add(cmbOrden);

        lblInfoOrden = new JLabel(" ");
        lblInfoOrden.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblInfoOrden.setForeground(new Color(173, 20, 87));
        lblInfoOrden.setBounds(10, 222, 285, 18);
        panel.add(lblInfoOrden);

        spnProducido = campoNumeroEn(
                panel, "Producido ahora (se suma al stock):", 10, 250, 280, 1);

        JLabel lblFecha = new JLabel("Fecha de produccion:");
        lblFecha.setBounds(10, 305, 200, 14);
        panel.add(lblFecha);

        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        spnFecha.setBounds(10, 325, 280, 24);
        panel.add(spnFecha);

        // ---------- STOCK ----------

        lblStockActual = new JLabel("Stock inicial:");
        lblStockActual.setBounds(10, 360, 135, 14);
        panel.add(lblStockActual);

        spnStockActual = new JSpinner(
                new SpinnerNumberModel(0, 0, 999999999, 1));

        FormatoUtil.aplicarMiles(spnStockActual);
        spnStockActual.setBounds(10, 380, 135, 24);
        panel.add(spnStockActual);

        spnStockMinimo = campoNumeroEn(
                panel, "Stock minimo:", 155, 360, 135, 1);

        spnPrecioMinorista = campoNumeroEn(
                panel, "Precio minorista:", 10, 415, 135, 1000);

        spnPrecioMayorista = campoNumeroEn(
                panel, "Precio mayorista:", 155, 415, 135, 1000);

        // ---------- BOTONES ----------

        JSeparator sep = new JSeparator();
        sep.setBounds(10, 475, 280, 6);
        panel.add(sep);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(10, 495, 130, 32);
        btnGuardar.addActionListener(e -> guardar());
        panel.add(btnGuardar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(160, 495, 130, 32);
        btnEditar.addActionListener(e -> editar());
        panel.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(10, 540, 130, 32);
        btnEliminar.addActionListener(e -> eliminar());
        panel.add(btnEliminar);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBounds(160, 540, 130, 32);
        btnSalir.addActionListener(e -> salir());
        panel.add(btnSalir);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(95, 580, 110, 32);
        btnCancelar.addActionListener(e -> limpiar());
        panel.add(btnCancelar);

        // ---------- BUSCADOR ----------

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setBounds(330, 18, 50, 22);
        getContentPane().add(lblBuscar);

        txtBuscar = new JTextField();
        txtBuscar.setBounds(385, 18, 300, 22);
        getContentPane().add(txtBuscar);

        JLabel lblIconoBuscar = new JLabel("\uD83D\uDD0D");
        lblIconoBuscar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIconoBuscar.setBounds(690, 15, 30, 25);
        getContentPane().add(lblIconoBuscar);

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

        modelo = new DefaultTableModel(new Object[] {
                "ID", "Nombre", "Color", "Categoria", "Orden",
                "Estimada", "Producida", "Pendiente", "Stock",
                "Minimo", "P. Minorista", "P. Mayorista"
        }, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(modelo);

        for (int col = 5; col <= 11; col++) {
            table.getColumnModel().getColumn(col)
                    .setCellRenderer(FormatoUtil.rendererMiles());
        }

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarRegistro();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(330, 55, 895, 578);
        getContentPane().add(sp);

        cargarCombos();
        cargarTabla();
    }

    // ---------- CONSTRUCCION DE CAMPOS ----------

    private JTextField campoTexto(JPanel p, String etiqueta, int y) {

        JLabel l = new JLabel(etiqueta);
        l.setBounds(10, y, 200, 14);
        p.add(l);

        JTextField t = new JTextField();
        t.setBounds(10, y + 20, 280, 24);
        p.add(t);

        return t;
    }

    private JSpinner campoNumeroEn(
            JPanel p, String etiqueta, int x, int y, int ancho, int paso) {

        JLabel l = new JLabel(etiqueta);
        l.setBounds(x, y, ancho, 14);
        p.add(l);

        JSpinner s = new JSpinner(
                new SpinnerNumberModel(0, 0, 999999999, paso));

        FormatoUtil.aplicarMiles(s);

        s.setBounds(x, y + 20, ancho, 24);
        p.add(s);

        return s;
    }

    // ---------- CARGA DE DATOS ----------

    private void cargarCombos() {

        List<Categoria_Producto> cats =
                categoriaDao.recuperarPorFiltro("");

        cmbCategoria.setModel(
                new DefaultComboBoxModel<>(cats.toArray(new Categoria_Producto[0])));

        cargarOrdenes(null);
    }

    private void cargarOrdenes(Orden_Produccion incluir) {

        List<Orden_Produccion> lista = ordenDao.recuperarPendientes();

        DefaultComboBoxModel<Orden_Produccion> m =
                new DefaultComboBoxModel<>();

        m.addElement(null);

        boolean incluida = (incluir == null);

        for (Orden_Produccion o : lista) {

            m.addElement(o);

            if (incluir != null
                    && o.getId_orden_produccion()
                    == incluir.getId_orden_produccion()) {

                incluida = true;
            }
        }

        if (!incluida) {
            m.addElement(incluir);
        }

        cmbOrden.setModel(m);
        actualizarInfoOrden();
    }

    private void actualizarInfoOrden() {

        if (lblInfoOrden == null || spnProducido == null) {
            return;
        }

        Orden_Produccion o =
                (Orden_Produccion) cmbOrden.getSelectedItem();

        SpinnerNumberModel m =
                (SpinnerNumberModel) spnProducido.getModel();

        if (o == null) {

            lblInfoOrden.setText("Sin orden: no se registra produccion");

            m.setMaximum(0);
            spnProducido.setValue(0);
            spnProducido.setEnabled(false);

        } else {

            int pendiente = Math.max(0, o.getPendiente());

            lblInfoOrden.setText(
                    "Estimada: " + FormatoUtil.miles(o.getCantidad_estimada())
                    + " | Producida: " + FormatoUtil.miles(o.getProducida())
                    + " | Pendiente: " + FormatoUtil.miles(pendiente));

            m.setMaximum(pendiente);

            if (((Number) spnProducido.getValue()).intValue() > pendiente) {
                spnProducido.setValue(0);
            }

            spnProducido.setEnabled(pendiente > 0);
        }
    }

    private void seleccionarOrden(Orden_Produccion o) {

        if (o == null) {
            cmbOrden.setSelectedIndex(0);
            return;
        }

        for (int i = 1; i < cmbOrden.getItemCount(); i++) {

            Orden_Produccion item = cmbOrden.getItemAt(i);

            if (item != null
                    && item.getId_orden_produccion()
                    == o.getId_orden_produccion()) {

                cmbOrden.setSelectedIndex(i);
                return;
            }
        }

        cmbOrden.setSelectedIndex(0);
    }

    // ---------- TABLA ----------

    private void cargarTabla() {

        String filtro = txtBuscar == null
                ? ""
                : txtBuscar.getText().trim();

        modelo.setRowCount(0);

        List<Producto_Terminado> productos =
                dao.recuperarPorFiltro(filtro);

        for (Producto_Terminado p : productos) {

            Orden_Produccion o = p.getOrden_produccion();

            String categoria = p.getCategoria_producto() == null
                    ? "-"
                    : p.getCategoria_producto().getNombre_categoria();

            modelo.addRow(new Object[] {
                    p.getId_producto_terminado(),
                    p.getNombre(),
                    p.getColor(),
                    categoria,
                    o == null ? "-" : "N° " + o.getId_orden_produccion(),
                    o == null ? "-" : o.getCantidad_estimada(),
                    o == null ? "-" : o.getProducida(),
                    o == null ? "-" : o.getPendiente(),
                    p.getStock_actual(),
                    p.getStock_minimo(),
                    p.getPrecio_minorista(),
                    p.getPrecio_mayorista()
            });
        }
    }

    private void seleccionarRegistro() {

        int fila = table.getSelectedRow();

        if (fila < 0) {
            seleccionado = null;
            return;
        }

        int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

        seleccionado = dao.recuperarPorId(id);
    }

    // ---------- EDITAR ----------

    private void editar() {

        if (table.getSelectedRow() < 0 || seleccionado == null) {

            JOptionPane.showMessageDialog(
                    this, "Seleccione un producto de la tabla para editar");

            return;
        }

        modoEdicion = true;

        cargarFormularioDesdeTabla();

        JOptionPane.showMessageDialog(
                this, "Puede modificar los datos del producto seleccionado");
    }

    private void cargarFormularioDesdeTabla() {

        if (seleccionado == null) {
            return;
        }

        txtNombre.setText(seleccionado.getNombre());
        txtColor.setText(seleccionado.getColor());

        // El stock actual se muestra, pero no se puede modificar.
        lblStockActual.setText("Stock actual (solo consulta):");
        spnStockActual.setValue(seleccionado.getStock_actual());
        spnStockActual.setEnabled(false);

        spnStockMinimo.setValue(seleccionado.getStock_minimo());
        spnPrecioMinorista.setValue(seleccionado.getPrecio_minorista());
        spnPrecioMayorista.setValue(seleccionado.getPrecio_mayorista());

        // No se registra producción nueva automáticamente.
        spnProducido.setValue(0);

        if (seleccionado.getCategoria_producto() != null) {

            int idCategoria =
                    seleccionado.getCategoria_producto().getId_categoria_producto();

            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {

                Categoria_Producto cat = cmbCategoria.getItemAt(i);

                if (cat.getId_categoria_producto() == idCategoria) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }

        cargarOrdenes(seleccionado.getOrden_produccion());
        seleccionarOrden(seleccionado.getOrden_produccion());
    }

    // ---------- GUARDAR ----------

    private void guardar() {

        String nombre = txtNombre.getText().trim();
        String color = txtColor.getText().trim();

        if (nombre.isEmpty() || color.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Nombre y color son obligatorios");

            return;
        }

        Categoria_Producto cat =
                (Categoria_Producto) cmbCategoria.getSelectedItem();

        if (cat == null) {

            JOptionPane.showMessageDialog(
                    this, "Primero cargue una categoria (menu Categoria de Producto)");

            return;
        }

        Orden_Produccion orden =
                (Orden_Produccion) cmbOrden.getSelectedItem();

        int producido = (orden != null && spnProducido.isEnabled())
                ? ((Number) spnProducido.getValue()).intValue()
                : 0;

        Date fecha = new Date(
                ((java.util.Date) spnFecha.getValue()).getTime());

        try {

            Producto_Terminado p =
                    modoEdicion ? seleccionado : new Producto_Terminado();

            if (p == null) {

                JOptionPane.showMessageDialog(
                        this, "No se encontro el producto que desea editar");

                return;
            }

            p.setNombre(nombre);
            p.setColor(color);
            p.setCategoria_producto(cat);

            /*
             * Al crear:
             * Se establece el stock inicial.
             * El DAO debe sumar las unidades producidas a ese valor.
             *
             * Al editar:
             * Se conserva el stock actual recuperado de la base de datos.
             * El DAO solamente lo incrementará si se registra producción.
             */
            if (!modoEdicion) {

                int stockInicial =
                        ((Number) spnStockActual.getValue()).intValue();

                p.setStock_actual(stockInicial);
            }

            p.setStock_minimo(
                    ((Number) spnStockMinimo.getValue()).intValue());

            p.setPrecio_minorista(
                    ((Number) spnPrecioMinorista.getValue()).intValue());

            p.setPrecio_mayorista(
                    ((Number) spnPrecioMayorista.getValue()).intValue());

            dao.guardarConProduccion(p, orden, producido, fecha);

            String mensaje;

            if (modoEdicion) {
                mensaje = "Producto terminado editado correctamente";
            } else {
                mensaje = "Producto terminado guardado correctamente";
            }

            if (orden != null && producido > 0) {

                int pendiente = Math.max(
                        0, orden.getPendiente() - producido);

                mensaje += "\nSe sumaron "
                        + FormatoUtil.miles(producido)
                        + " unidades al stock de " + nombre + ".";

                if (pendiente == 0) {

                    mensaje += "\nLa orden N° "
                            + orden.getId_orden_produccion()
                            + " quedo completada.";

                } else {

                    mensaje += "\nQuedan "
                            + FormatoUtil.miles(pendiente)
                            + " unidades pendientes de la orden N° "
                            + orden.getId_orden_produccion() + ".";
                }
            }

            JOptionPane.showMessageDialog(this, mensaje);

            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se pudo guardar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- ELIMINAR ----------

    private void eliminar() {

        if (seleccionado == null) {

            JOptionPane.showMessageDialog(
                    this, "Seleccione un producto de la tabla");

            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar \"" + seleccionado.getNombre() + "\"?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            dao.eliminar(seleccionado);

            JOptionPane.showMessageDialog(
                    this, "Producto terminado eliminado correctamente");

            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error al eliminar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- LIMPIAR ----------

    private void limpiar() {

        seleccionado = null;
        modoEdicion = false;

        txtNombre.setText("");
        txtColor.setText("");

        spnProducido.setValue(0);

        // Al volver al registro nuevo, se habilita el stock inicial.
        lblStockActual.setText("Stock inicial:");
        spnStockActual.setValue(0);
        spnStockActual.setEnabled(true);

        spnStockMinimo.setValue(0);
        spnPrecioMinorista.setValue(0);
        spnPrecioMayorista.setValue(0);

        table.clearSelection();

        cargarCombos();

        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }

        cmbOrden.setSelectedIndex(0);
    }

    // ---------- SALIR ----------

    private void salir() {
        dispose();
    }
}

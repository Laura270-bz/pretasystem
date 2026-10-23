package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_dao.Producto_Terminado_Dao;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_entidades.Producto_Terminado;

/** ABM COMPUESTO: Producto Terminado (con combo de Categoria y de Orden de Produccion). */
public class ProductoTerminadoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtNombre;
	private JTextField txtColor;
	private JComboBox<Categoria_Producto> cmbCategoria;
	private JComboBox<Orden_Produccion> cmbOrden;
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
		setBounds(100, 100, 1100, 680);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// FORMULARIO 
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
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
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
				super.getListCellRendererComponent(l, v, i, s, f);
				setText(v instanceof Categoria_Producto ? ((Categoria_Producto) v).getNombre_categoria() : "");
				return this;
			}
		});
		panel.add(cmbCategoria);

		JLabel lblOrden = new JLabel("Orden de produccion:");
		lblOrden.setBounds(10, 175, 200, 14);
		panel.add(lblOrden);
		cmbOrden = new JComboBox<>();
		cmbOrden.setBounds(10, 195, 280, 24);
		cmbOrden.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
				super.getListCellRendererComponent(l, v, i, s, f);
				if (v instanceof Orden_Produccion) {
					Orden_Produccion o = (Orden_Produccion) v;
					setText("Orden N° " + o.getId_orden_produccion() + " - " + o.getEtapa_actual() + " - "
							+ o.getFecha_inicio());
				} else {
					setText("(Sin orden)");
				}
				return this;
			}
		});
		panel.add(cmbOrden);

		spnStockActual = campoNumero(panel, "Stock actual:", 230, 1);
		spnStockMinimo = campoNumero(panel, "Stock minimo:", 285, 1);
		spnPrecioMinorista = campoNumero(panel, "Precio minorista:", 340, 1000);
		spnPrecioMayorista = campoNumero(panel, "Precio mayorista:", 395, 1000);

		JSeparator sep = new JSeparator();
		sep.setBounds(10, 480, 280, 6);
		panel.add(sep);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(10, 514, 110, 32);
		btnGuardar.addActionListener(e -> guardar());
		panel.add(btnGuardar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(180, 514, 110, 32);
		btnEliminar.addActionListener(e -> eliminar());
		panel.add(btnEliminar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(95, 575, 110, 32);
		btnCancelar.addActionListener(e -> limpiar());
		panel.add(btnCancelar);

		// ---------- BUSCADOR + TABLA ----------
		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setBounds(330, 18, 50, 22);
		getContentPane().add(lblBuscar);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(385, 18, 300, 22);
		getContentPane().add(txtBuscar);
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { cargarTabla(); }
			public void removeUpdate(DocumentEvent e) { cargarTabla(); }
			public void changedUpdate(DocumentEvent e) { cargarTabla(); }
		});

		modelo = new DefaultTableModel(new Object[] { "ID", "Nombre", "Color", "Categoria", "Orden", "Stock",
				"Minimo", "P. Minorista", "P. Mayorista" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(modelo);
		for (int col = 5; col <= 8; col++) { // Stock, Minimo, P. Minorista, P. Mayorista
			table.getColumnModel().getColumn(col).setCellRenderer(FormatoUtil.rendererMiles());
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				cargarFormularioDesdeTabla();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(330, 55, 745, 578);
		getContentPane().add(sp);

		cargarCombos();
		cargarTabla();
	}

	// ---------- helpers de construccion ----------
	private JTextField campoTexto(JPanel p, String etiqueta, int y) {
		JLabel l = new JLabel(etiqueta);
		l.setBounds(10, y, 200, 14);
		p.add(l);
		JTextField t = new JTextField();
		t.setBounds(10, y + 20, 280, 24);
		p.add(t);
		return t;
	}

	private JSpinner campoNumero(JPanel p, String etiqueta, int y, int paso) {
		JLabel l = new JLabel(etiqueta);
		l.setBounds(10, y, 200, 14);
		p.add(l);
		JSpinner s = new JSpinner(new SpinnerNumberModel(0, 0, 999999999, paso));
		FormatoUtil.aplicarMiles(s);
		s.setBounds(10, y + 20, 280, 24);
		p.add(s);
		return s;
	}

	// datos 
	private void cargarCombos() {
		List<Categoria_Producto> cats = categoriaDao.recuperarPorFiltro("");
		cmbCategoria.setModel(new DefaultComboBoxModel<>(cats.toArray(new Categoria_Producto[0])));

		List<Orden_Produccion> ordenes = ordenDao.recuperarPorFiltro("");
		DefaultComboBoxModel<Orden_Produccion> m = new DefaultComboBoxModel<>();
		m.addElement(null); // opcion "(Sin orden)"
		for (Orden_Produccion o : ordenes) {
			m.addElement(o);
		}
		cmbOrden.setModel(m);
	}

	private void cargarTabla() {
		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();
		modelo.setRowCount(0);
		for (Producto_Terminado p : dao.recuperarPorFiltro(filtro)) {
			modelo.addRow(new Object[] { p.getId_producto_terminado(), p.getNombre(), p.getColor(),
					p.getCategoria_producto().getNombre_categoria(),
					p.getOrden_produccion() == null ? "-" : "N° " + p.getOrden_produccion().getId_orden_produccion(),
					p.getStock_actual(), p.getStock_minimo(), p.getPrecio_minorista(), p.getPrecio_mayorista() });
		}
	}

	private void guardar() {
		String nombre = txtNombre.getText().trim();
		String color = txtColor.getText().trim();
		if (nombre.isEmpty() || color.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Nombre y color son obligatorios");
			return;
		}
		Categoria_Producto cat = (Categoria_Producto) cmbCategoria.getSelectedItem();
		if (cat == null) {
			JOptionPane.showMessageDialog(this, "Primero cargue una categoria (menu Categoria de Producto)");
			return;
		}
		try {
			Producto_Terminado p = seleccionado == null ? new Producto_Terminado() : seleccionado;
			p.setNombre(nombre);
			p.setColor(color);
			p.setCategoria_producto(cat);
			p.setOrden_produccion((Orden_Produccion) cmbOrden.getSelectedItem());
			p.setStock_actual(((Number) spnStockActual.getValue()).intValue());
			p.setStock_minimo(((Number) spnStockMinimo.getValue()).intValue());
			p.setPrecio_minorista(((Number) spnPrecioMinorista.getValue()).intValue());
			p.setPrecio_mayorista(((Number) spnPrecioMayorista.getValue()).intValue());

			dao.guardar(p);
			JOptionPane.showMessageDialog(this, "Guardado correctamente");
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
		}
	}

	private void eliminar() {
		if (seleccionado == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
			return;
		}
		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionado.getNombre() + "\"?", "Confirmar",
				JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			dao.eliminar(seleccionado);
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
		}
	}

	private void cargarFormularioDesdeTabla() {
		int fila = table.getSelectedRow();
		if (fila < 0) {
			return;
		}
		int id = ((Number) modelo.getValueAt(fila, 0)).intValue();
		seleccionado = dao.recuperarPorId(id);
		if (seleccionado == null) {
			return;
		}
		txtNombre.setText(seleccionado.getNombre());
		txtColor.setText(seleccionado.getColor());
		spnStockActual.setValue(seleccionado.getStock_actual());
		spnStockMinimo.setValue(seleccionado.getStock_minimo());
		spnPrecioMinorista.setValue(seleccionado.getPrecio_minorista());
		spnPrecioMayorista.setValue(seleccionado.getPrecio_mayorista());

		// seleccionar en los combos el item con el mismo id (son objetos de otra sesion)
		for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
			if (cmbCategoria.getItemAt(i).getId_categoria_producto() == seleccionado.getCategoria_producto()
					.getId_categoria_producto()) {
				cmbCategoria.setSelectedIndex(i);
				break;
			}
		}
		cmbOrden.setSelectedIndex(0);
		if (seleccionado.getOrden_produccion() != null) {
			for (int i = 1; i < cmbOrden.getItemCount(); i++) {
				if (cmbOrden.getItemAt(i).getId_orden_produccion() == seleccionado.getOrden_produccion()
						.getId_orden_produccion()) {
					cmbOrden.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void limpiar() {
		seleccionado = null;
		txtNombre.setText("");
		txtColor.setText("");
		spnStockActual.setValue(0);
		spnStockMinimo.setValue(0);
		spnPrecioMinorista.setValue(0);
		spnPrecioMayorista.setValue(0);
		if (cmbCategoria.getItemCount() > 0) {
			cmbCategoria.setSelectedIndex(0);
		}
		cmbOrden.setSelectedIndex(0);
		table.clearSelection();
		cargarCombos(); // por si se cargaron categorias u ordenes nuevas
	}
}
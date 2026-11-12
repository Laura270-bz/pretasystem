package facitec.edu.py.pretasystem_abms;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_dao.Producto_Terminado_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_entidades.Producto_Terminado;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_util.Iconos;

/**
 * ABM de Producto Terminado con el mismo diseño de la pantalla principal.
 */
public class ProductoTerminadoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
	private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

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
		setIconImage(Iconos.imagen("caja", 64, ROSA_FUERTE));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Sin opción de maximizar ni de cambiar el tamaño.
		setResizable(false);

		setSize(1300, 740);
		setLocationRelativeTo(null);

		setContentPane(new FondoPanel());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
		getContentPane().add(crearCuerpo(), BorderLayout.CENTER);

		cargarCombos();
		cargarTabla();
	}

	// ====================== DISEÑO ======================

	private JPanel crearEncabezado() {

		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(18, 30, 4, 30));

		JLabel titulo = new JLabel("Producto Terminado");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titulo.setForeground(ROSA_OSCURO);

		JLabel sub = new JLabel("Registro de productos, producción y stock");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		sub.setForeground(new Color(150, 80, 110));

		p.add(titulo);
		p.add(sub);

		return p;
	}

	private JPanel crearCuerpo() {

		JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
		cuerpo.setOpaque(false);
		cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 30, 25, 30));

		cuerpo.add(crearFormulario(), BorderLayout.WEST);
		cuerpo.add(crearPanelTabla(), BorderLayout.CENTER);

		return cuerpo;
	}

	private JPanel crearFormulario() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new GridBagLayout());
		card.setPreferredSize(new Dimension(390, 0));
		card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

		// Título del formulario (ocupa las dos columnas).
		JLabel lblForm = new JLabel("Datos del producto");
		lblForm.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblForm.setForeground(ROSA_OSCURO);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(0, 0, 10, 0);
		card.add(lblForm, c);

		// Fila 0: nombre | color
		txtNombre = new JTextField();
		estilizarCampo(txtNombre);
		txtColor = new JTextField();
		estilizarCampo(txtColor);

		addCampo(card, etiqueta("Nombre"), txtNombre, 0, 0, 1);
		addCampo(card, etiqueta("Color"), txtColor, 1, 0, 1);

		// Fila 1: categoría
		cmbCategoria = new JComboBox<>();
		estilizarCombo(cmbCategoria);
		cmbCategoria.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {

				super.getListCellRendererComponent(l, v, i, s, f);

				setText(v instanceof Categoria_Producto ? ((Categoria_Producto) v).getNombre_categoria() : "");

				return this;
			}
		});

		addCampo(card, etiqueta("Categoría"), cmbCategoria, 0, 1, 2);

		// Fila 2: orden de producción + información de la orden
		cmbOrden = new JComboBox<>();
		estilizarCombo(cmbOrden);
		cmbOrden.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {

				super.getListCellRendererComponent(l, v, i, s, f);

				if (v instanceof Orden_Produccion) {
					Orden_Produccion o = (Orden_Produccion) v;

					setText("Orden N° " + o.getId_orden_produccion() + " - " + o.getEtapa_actual() + " - pendiente: "
							+ FormatoUtil.miles(o.getPendiente()));

				} else {
					setText("(Sin orden)");
				}

				return this;
			}
		});

		cmbOrden.addActionListener(e -> actualizarInfoOrden());

		lblInfoOrden = new JLabel(" ");
		lblInfoOrden.setFont(new Font("Segoe UI", Font.BOLD, 11));
		lblInfoOrden.setForeground(ROSA_OSCURO);

		JPanel panelOrden = new JPanel(new BorderLayout(0, 3));
		panelOrden.setOpaque(false);
		panelOrden.add(cmbOrden, BorderLayout.NORTH);
		panelOrden.add(lblInfoOrden, BorderLayout.SOUTH);

		addCampo(card, etiqueta("Orden de producción"), panelOrden, 0, 2, 2);

		// Fila 3: producido | fecha
		spnProducido = campoNumero(1);
		spnFecha = new JSpinner(new SpinnerDateModel());
		spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
		estilizarSpinner(spnFecha);

		addCampo(card, etiqueta("Producido ahora"), spnProducido, 0, 3, 1);
		addCampo(card, etiqueta("Fecha de producción"), spnFecha, 1, 3, 1);

		// Fila 4: stock actual | stock mínimo
		lblStockActual = etiqueta("Stock inicial");
		spnStockActual = campoNumero(1);
		spnStockMinimo = campoNumero(1);

		addCampo(card, lblStockActual, spnStockActual, 0, 4, 1);
		addCampo(card, etiqueta("Stock mínimo"), spnStockMinimo, 1, 4, 1);

		// Fila 5: precios
		spnPrecioMinorista = campoNumero(1000);
		spnPrecioMayorista = campoNumero(1000);

		addCampo(card, etiqueta("Precio minorista"), spnPrecioMinorista, 0, 5, 1);
		addCampo(card, etiqueta("Precio mayorista"), spnPrecioMayorista, 1, 5, 1);

		// ---------- BOTONES ----------

		JPanel botones = new JPanel(new GridLayout(2, 2, 10, 10));
		botones.setOpaque(false);

		BotonRosa btnGuardar = new BotonRosa("Guardar", true);
		btnGuardar.addActionListener(e -> guardar());
		botones.add(btnGuardar);

		BotonRosa btnEditar = new BotonRosa("Editar", true);
		btnEditar.addActionListener(e -> editar());
		botones.add(btnEditar);

		BotonRosa btnEliminar = new BotonRosa("Eliminar", true);
		btnEliminar.addActionListener(e -> eliminar());
		botones.add(btnEliminar);

		BotonRosa btnCancelar = new BotonRosa("Cancelar", false);
		btnCancelar.addActionListener(e -> limpiar());
		botones.add(btnCancelar);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(8, 0, 10, 0);
		card.add(botones, c);

		BotonRosa btnSalir = new BotonRosa("Salir", false);
		btnSalir.addActionListener(e -> salir());
		c.gridy = 14;
		c.insets = new Insets(0, 0, 0, 0);
		card.add(btnSalir, c);

		// Empuja todo hacia arriba.
		c.gridy = 15;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JPanel relleno = new JPanel();
		relleno.setOpaque(false);
		card.add(relleno, c);

		return card;
	}

	/**
	 * Agrega una etiqueta y su componente en la cuadrícula del formulario. Cada
	 * "fila" usa dos filas de la grilla (etiqueta arriba, campo abajo).
	 */
	private void addCampo(JPanel card, JLabel lbl, JComponent comp, int col, int fila, int span) {

		boolean izquierda = (col == 0 && span == 1);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridwidth = span;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.gridy = 1 + fila * 2;
		c.insets = new Insets(0, 0, 3, izquierda ? 10 : 0);
		card.add(lbl, c);

		c.gridy = 2 + fila * 2;
		c.insets = new Insets(0, 0, 12, izquierda ? 10 : 0);
		card.add(comp, c);
	}

	private JPanel crearPanelTabla() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new BorderLayout(0, 12));
		card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

		// ---------- BUSCADOR ----------

		JPanel buscador = new JPanel(new BorderLayout(10, 0));
		buscador.setOpaque(false);

		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setFont(FUENTE_LABEL);
		lblBuscar.setForeground(ROSA_OSCURO);
		buscador.add(lblBuscar, BorderLayout.WEST);

		txtBuscar = new JTextField();
		estilizarCampo(txtBuscar);
		buscador.add(txtBuscar, BorderLayout.CENTER);

		JLabel lblIconoBuscar = new JLabel("\uD83D\uDD0D");
		lblIconoBuscar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
		buscador.add(lblIconoBuscar, BorderLayout.EAST);

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

		card.add(buscador, BorderLayout.NORTH);

		// ---------- TABLA ----------

		modelo = new DefaultTableModel(new Object[] { "ID", "Nombre", "Color", "Categoría", "Orden", "Estimada",
				"Producida", "Pendiente", "Stock", "Mínimo", "P. Minorista", "P. Mayorista" }, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		table = new JTable(modelo);
		table.setFont(FUENTE_CAMPO);
		table.setRowHeight(28);
		table.setShowVerticalLines(false);
		table.setGridColor(ROSA_CLARO);
		table.setSelectionBackground(ROSA_MEDIO);
		table.setSelectionForeground(Color.WHITE);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DefaultTableCellRenderer header = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r,
					int col) {

				JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, col);

				l.setBackground(ROSA_FUERTE);
				l.setForeground(Color.WHITE);
				l.setFont(new Font("Segoe UI", Font.BOLD, 12));
				l.setHorizontalAlignment(SwingConstants.CENTER);
				l.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
				l.setOpaque(true);

				return l;
			}
		};

		table.getTableHeader().setDefaultRenderer(header);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(0, 34));

		for (int col = 5; col <= 11; col++) {
			table.getColumnModel().getColumn(col).setCellRenderer(FormatoUtil.rendererMiles());
		}

		int[] anchos = { 35, 120, 70, 90, 55, 70, 70, 70, 60, 60, 85, 85 };
		for (int i = 0; i < anchos.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
		}

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				seleccionarRegistro();
			}
		});

		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
		sp.getViewport().setBackground(Color.WHITE);

		card.add(sp, BorderLayout.CENTER);

		return card;
	}

	// ---------- ESTILOS ----------

	private JLabel etiqueta(String texto) {

		JLabel l = new JLabel(texto);
		l.setFont(FUENTE_LABEL);
		l.setForeground(ROSA_OSCURO);

		return l;
	}

	private void estilizarCampo(JTextField t) {

		t.setFont(FUENTE_CAMPO);
		t.setPreferredSize(new Dimension(100, 32));
		t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO),
				BorderFactory.createEmptyBorder(2, 8, 2, 8)));
	}

	private void estilizarSpinner(JSpinner s) {

		s.setFont(FUENTE_CAMPO);
		s.setPreferredSize(new Dimension(100, 32));
		s.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
	}

	private void estilizarCombo(JComboBox<?> c) {

		c.setFont(FUENTE_CAMPO);
		c.setBackground(Color.WHITE);
		c.setPreferredSize(new Dimension(100, 32));
	}

	private JSpinner campoNumero(int paso) {

		JSpinner s = new JSpinner(new SpinnerNumberModel(0, 0, 999999999, paso));
		FormatoUtil.aplicarMiles(s);
		estilizarSpinner(s);

		return s;
	}

	// ====================== CARGA DE DATOS ======================

	private void cargarCombos() {

		List<Categoria_Producto> cats = categoriaDao.recuperarPorFiltro("");

		cmbCategoria.setModel(new DefaultComboBoxModel<>(cats.toArray(new Categoria_Producto[0])));

		cargarOrdenes(null);
	}

	private void cargarOrdenes(Orden_Produccion incluir) {

		List<Orden_Produccion> lista = ordenDao.recuperarPendientes();

		DefaultComboBoxModel<Orden_Produccion> m = new DefaultComboBoxModel<>();

		m.addElement(null);

		boolean incluida = (incluir == null);

		for (Orden_Produccion o : lista) {

			m.addElement(o);

			if (incluir != null && o.getId_orden_produccion() == incluir.getId_orden_produccion()) {

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

		Orden_Produccion o = (Orden_Produccion) cmbOrden.getSelectedItem();

		SpinnerNumberModel m = (SpinnerNumberModel) spnProducido.getModel();

		if (o == null) {

			lblInfoOrden.setText("Sin orden: no se registra producción");

			m.setMaximum(0);
			spnProducido.setValue(0);
			spnProducido.setEnabled(false);

		} else {

			int pendiente = Math.max(0, o.getPendiente());

			lblInfoOrden.setText("Estimada: " + FormatoUtil.miles(o.getCantidad_estimada()) + " | Producida: "
					+ FormatoUtil.miles(o.getProducida()) + " | Pendiente: " + FormatoUtil.miles(pendiente));

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

			if (item != null && item.getId_orden_produccion() == o.getId_orden_produccion()) {

				cmbOrden.setSelectedIndex(i);
				return;
			}
		}

		cmbOrden.setSelectedIndex(0);
	}

	// ====================== TABLA ======================

	private void cargarTabla() {

		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();

		modelo.setRowCount(0);

		List<Producto_Terminado> productos = dao.recuperarPorFiltro(filtro);

		for (Producto_Terminado p : productos) {

			Orden_Produccion o = p.getOrden_produccion();

			String categoria = p.getCategoria_producto() == null ? "-"
					: p.getCategoria_producto().getNombre_categoria();

			modelo.addRow(new Object[] { p.getId_producto_terminado(), p.getNombre(), p.getColor(), categoria,
					o == null ? "-" : "N° " + o.getId_orden_produccion(), o == null ? "-" : o.getCantidad_estimada(),
					o == null ? "-" : o.getProducida(), o == null ? "-" : o.getPendiente(), p.getStock_actual(),
					p.getStock_minimo(), p.getPrecio_minorista(), p.getPrecio_mayorista() });
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

	// ====================== EDITAR ======================

	private void editar() {

		if (table.getSelectedRow() < 0 || seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para editar");

			return;
		}

		modoEdicion = true;

		cargarFormularioDesdeTabla();

		JOptionPane.showMessageDialog(this, "Puede modificar los datos del producto seleccionado");
	}

	private void cargarFormularioDesdeTabla() {

		if (seleccionado == null) {
			return;
		}

		txtNombre.setText(seleccionado.getNombre());
		txtColor.setText(seleccionado.getColor());

		// El stock actual se muestra, pero no se puede modificar.
		lblStockActual.setText("Stock actual (consulta)");
		spnStockActual.setValue(seleccionado.getStock_actual());
		spnStockActual.setEnabled(false);

		spnStockMinimo.setValue(seleccionado.getStock_minimo());
		spnPrecioMinorista.setValue(seleccionado.getPrecio_minorista());
		spnPrecioMayorista.setValue(seleccionado.getPrecio_mayorista());

		// No se registra producción nueva automáticamente.
		spnProducido.setValue(0);

		if (seleccionado.getCategoria_producto() != null) {

			int idCategoria = seleccionado.getCategoria_producto().getId_categoria_producto();

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

	// ====================== GUARDAR ======================

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

		Orden_Produccion orden = (Orden_Produccion) cmbOrden.getSelectedItem();

		int producido = (orden != null && spnProducido.isEnabled()) ? ((Number) spnProducido.getValue()).intValue()
				: 0;

		Date fecha = new Date(((java.util.Date) spnFecha.getValue()).getTime());

		try {

			Producto_Terminado p = modoEdicion ? seleccionado : new Producto_Terminado();

			if (p == null) {

				JOptionPane.showMessageDialog(this, "No se encontro el producto que desea editar");

				return;
			}

			p.setNombre(nombre);
			p.setColor(color);
			p.setCategoria_producto(cat);

			/*
			 * Al crear: se establece el stock inicial. El DAO debe sumar las unidades
			 * producidas a ese valor.
			 *
			 * Al editar: se conserva el stock actual recuperado de la base de datos. El DAO
			 * solamente lo incrementará si se registra producción.
			 */
			if (!modoEdicion) {

				int stockInicial = ((Number) spnStockActual.getValue()).intValue();

				p.setStock_actual(stockInicial);
			}

			p.setStock_minimo(((Number) spnStockMinimo.getValue()).intValue());

			p.setPrecio_minorista(((Number) spnPrecioMinorista.getValue()).intValue());

			p.setPrecio_mayorista(((Number) spnPrecioMayorista.getValue()).intValue());

			dao.guardarConProduccion(p, orden, producido, fecha);

			String mensaje;

			if (modoEdicion) {
				mensaje = "Producto terminado editado correctamente";
			} else {
				mensaje = "Producto terminado guardado correctamente";
			}

			if (orden != null && producido > 0) {

				int pendiente = Math.max(0, orden.getPendiente() - producido);

				mensaje += "\nSe sumaron " + FormatoUtil.miles(producido) + " unidades al stock de " + nombre + ".";

				if (pendiente == 0) {

					mensaje += "\nLa orden N° " + orden.getId_orden_produccion() + " quedo completada.";

				} else {

					mensaje += "\nQuedan " + FormatoUtil.miles(pendiente) + " unidades pendientes de la orden N° "
							+ orden.getId_orden_produccion() + ".";
				}
			}

			JOptionPane.showMessageDialog(this, mensaje);

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ====================== ELIMINAR ======================

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

			JOptionPane.showMessageDialog(this, "Producto terminado eliminado correctamente");

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ====================== LIMPIAR ======================

	private void limpiar() {

		seleccionado = null;
		modoEdicion = false;

		txtNombre.setText("");
		txtColor.setText("");

		spnProducido.setValue(0);

		// Al volver al registro nuevo, se habilita el stock inicial.
		lblStockActual.setText("Stock inicial");
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

	// ====================== SALIR ======================

	private void salir() {
		dispose();
	}

	// ====================== COMPONENTES VISUALES ======================

	/** Fondo degradado rosa, igual al de la pantalla principal. */
	private static class FondoPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			g2.setPaint(new GradientPaint(0, 0, new Color(255, 245, 248), 0, h, new Color(250, 205, 222)));
			g2.fillRect(0, 0, w, h);

			g2.setColor(new Color(255, 255, 255, 120));
			g2.fillOval(-120, -120, 380, 380);
			g2.fillOval(w - 260, h - 240, 420, 420);

			g2.setColor(new Color(244, 143, 177, 45));
			g2.fillOval(w - 200, -90, 280, 280);
			g2.fillOval(-80, h - 200, 260, 260);

			g2.dispose();
		}
	}

	/** Panel blanco con esquinas redondeadas y borde rosa. */
	private static class TarjetaPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		TarjetaPanel() {
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth() - 6;
			int h = getHeight() - 6;

			g2.setColor(new Color(173, 20, 87, 35));
			g2.fillRoundRect(4, 6, w, h, 28, 28);

			g2.setColor(new Color(255, 255, 255, 240));
			g2.fillRoundRect(0, 0, w, h, 28, 28);

			g2.setColor(ROSA_MEDIO);
			g2.setStroke(new BasicStroke(2f));
			g2.drawRoundRect(0, 0, w, h, 28, 28);

			g2.dispose();

			super.paintComponent(g);
		}
	}

	/** Botón redondeado: relleno rosa (principal) o con borde (secundario). */
	private static class BotonRosa extends JButton {

		private static final long serialVersionUID = 1L;

		private final boolean principal;

		BotonRosa(String texto, boolean principal) {

			super(texto);

			this.principal = principal;

			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setForeground(principal ? Color.WHITE : ROSA_OSCURO);
			setFont(new Font("Segoe UI", Font.BOLD, 13));
			setPreferredSize(new Dimension(100, 36));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			boolean hover = getModel().isRollover() || getModel().isArmed();

			if (principal) {
				g2.setColor(hover ? ROSA_OSCURO : ROSA_FUERTE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
			} else {
				g2.setColor(hover ? ROSA_CLARO : Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.setColor(ROSA_FUERTE);
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
			}

			g2.dispose();

			super.paintComponent(g);
		}
	}
}
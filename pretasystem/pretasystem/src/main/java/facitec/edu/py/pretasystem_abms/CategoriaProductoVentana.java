
package facitec.edu.py.pretasystem_abms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;

public class CategoriaProductoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	// ====================== COLORES ======================

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);
	private static final Color TEXTO = new Color(90, 55, 70);
	private static final Color FONDO_TABLA = new Color(255, 248, 251);

	// ====================== COMPONENTES ======================

	private JTextField txtNombre;
	private JTextField txtBuscar;

	private JButton btnGuardar;
	private JButton btnEditar;
	private JButton btnEliminar;
	private JButton btnCancelar;

	private JTable table;
	private DefaultTableModel modelo;

	private final Categoria_Producto_Dao dao = new Categoria_Producto_Dao();

	private Categoria_Producto seleccionada = null;

	private boolean cargando = false;

	// ====================== INICIO ======================

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new CategoriaProductoVentana().setVisible(true));
	}

	public CategoriaProductoVentana() {

		setTitle("PretaSystem - Categorías de Productos");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Ventana de tamaño fijo, sin opción de maximizar.
		setSize(900, 650);
		setResizable(false);
		setLocationRelativeTo(null);

		FondoPanel fondo = new FondoPanel();
		fondo.setLayout(new BorderLayout());

		setContentPane(fondo);

		fondo.add(crearEncabezado(), BorderLayout.NORTH);
		fondo.add(crearContenido(), BorderLayout.CENTER);

		cargarTabla();
		modoNuevo();
	}

	// ====================== ENCABEZADO ======================

	private JPanel crearEncabezado() {

		JPanel encabezado = new JPanel(new BorderLayout());

		encabezado.setOpaque(false);
		encabezado.setBorder(BorderFactory.createEmptyBorder(25, 35, 15, 35));

		JPanel textos = new JPanel(new GridLayout(2, 1));

		textos.setOpaque(false);

		JLabel titulo = new JLabel("Categorías de Productos");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titulo.setForeground(ROSA_OSCURO);

		JLabel subtitulo = new JLabel("Administración y organización de categorías");

		subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitulo.setForeground(new Color(150, 80, 110));

		textos.add(titulo);
		textos.add(subtitulo);

		encabezado.add(textos, BorderLayout.WEST);

		BotonRosa btnSalir = new BotonRosa("Salir");

		btnSalir.setPreferredSize(new Dimension(110, 38));

		btnSalir.addActionListener(e -> dispose());

		JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));

		derecha.setOpaque(false);
		derecha.add(btnSalir);

		encabezado.add(derecha, BorderLayout.EAST);

		return encabezado;
	}

	// ====================== CONTENIDO PRINCIPAL ======================

	private JPanel crearContenido() {

		PanelTarjeta tarjeta = new PanelTarjeta();

		tarjeta.setLayout(new BorderLayout(0, 15));

		tarjeta.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

		tarjeta.add(crearFormulario(), BorderLayout.NORTH);
		tarjeta.add(crearTabla(), BorderLayout.CENTER);

		JPanel contenedor = new JPanel(new BorderLayout());

		contenedor.setOpaque(false);

		contenedor.setBorder(BorderFactory.createEmptyBorder(10, 35, 30, 35));

		contenedor.add(tarjeta, BorderLayout.CENTER);

		return contenedor;
	}

	// ====================== FORMULARIO ======================

	private JPanel crearFormulario() {

		JPanel panel = new JPanel(new BorderLayout(0, 12));

		panel.setOpaque(false);

		// Título de sección.

		JLabel titulo = new JLabel("Datos de la categoría");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		titulo.setForeground(ROSA_OSCURO);

		panel.add(titulo, BorderLayout.NORTH);

		// Formulario.

		JPanel formulario = new JPanel(new BorderLayout(12, 0));

		formulario.setOpaque(false);

		JPanel campo = new JPanel(new BorderLayout(0, 7));

		campo.setOpaque(false);

		JLabel lblNombre = new JLabel("Nombre de la categoría");

		lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblNombre.setForeground(TEXTO);

		txtNombre = new JTextField();

		txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtNombre.setPreferredSize(new Dimension(300, 38));

		txtNombre.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		txtNombre.addActionListener(e -> guardar());

		campo.add(lblNombre, BorderLayout.NORTH);
		campo.add(txtNombre, BorderLayout.CENTER);

		formulario.add(campo, BorderLayout.CENTER);

		// Botones del formulario.

		JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 19));

		botones.setOpaque(false);

		btnGuardar = new BotonRosa("Guardar");
		btnCancelar = new BotonSecundario("Limpiar");

		btnGuardar.setPreferredSize(new Dimension(105, 38));
		btnCancelar.setPreferredSize(new Dimension(105, 38));

		btnGuardar.addActionListener(e -> guardar());
		btnCancelar.addActionListener(e -> modoNuevo());

		botones.add(btnGuardar);
		botones.add(btnCancelar);

		formulario.add(botones, BorderLayout.EAST);

		panel.add(formulario, BorderLayout.CENTER);

		return panel;
	}

	// ====================== TABLA Y BUSCADOR ======================

	private JPanel crearTabla() {

		JPanel panel = new JPanel(new BorderLayout(0, 12));

		panel.setOpaque(false);

		// Encabezado de la tabla.

		JPanel superior = new JPanel(new BorderLayout());

		superior.setOpaque(false);

		JLabel titulo = new JLabel("Listado de categorías");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		titulo.setForeground(ROSA_OSCURO);

		superior.add(titulo, BorderLayout.WEST);

		// Buscador.

		JPanel busqueda = new JPanel(new BorderLayout(8, 0));

		busqueda.setOpaque(false);

		JLabel lblBuscar = new JLabel("Buscar:");

		lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblBuscar.setForeground(TEXTO);

		txtBuscar = new JTextField();

		txtBuscar.setPreferredSize(new Dimension(200, 34));
		txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO, 1),
				BorderFactory.createEmptyBorder(4, 9, 4, 9)));

		txtBuscar.setToolTipText("Buscar categoría por nombre");

		busqueda.add(lblBuscar, BorderLayout.WEST);
		busqueda.add(txtBuscar, BorderLayout.CENTER);

		superior.add(busqueda, BorderLayout.EAST);

		panel.add(superior, BorderLayout.NORTH);

		// Modelo de tabla.

		modelo = new DefaultTableModel(new Object[] { "ID", "Nombre de la categoría" }, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		table = new JTable(modelo);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.setRowHeight(34);

		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		table.setForeground(TEXTO);

		table.setBackground(Color.WHITE);

		table.setGridColor(new Color(245, 220, 231));

		table.setSelectionBackground(ROSA_CLARO);
		table.setSelectionForeground(ROSA_OSCURO);

		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(true);

		table.setFillsViewportHeight(true);

		// Encabezado de tabla.

		JTableHeader encabezado = table.getTableHeader();

		encabezado.setPreferredSize(new Dimension(0, 38));

		encabezado.setFont(new Font("Segoe UI", Font.BOLD, 13));

		encabezado.setBackground(ROSA_FUERTE);
		encabezado.setForeground(Color.WHITE);

		encabezado.setReorderingAllowed(false);

		// Alineación de columnas.

		DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();

		centrado.setHorizontalAlignment(SwingConstants.CENTER);

		table.getColumnModel().getColumn(0).setCellRenderer(centrado);

		table.getColumnModel().getColumn(0).setPreferredWidth(80);

		table.getColumnModel().getColumn(1).setPreferredWidth(500);

		// Scroll de la tabla.

		JScrollPane scroll = new JScrollPane(table);

		scroll.setBorder(BorderFactory.createLineBorder(new Color(245, 210, 225)));

		scroll.getViewport().setBackground(Color.WHITE);

		panel.add(scroll, BorderLayout.CENTER);

		// Botones inferiores.

		JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

		inferior.setOpaque(false);

		btnEditar = new BotonSecundario("Editar");
		btnEliminar = new BotonEliminar("Eliminar");

		btnEditar.setPreferredSize(new Dimension(115, 36));
		btnEliminar.setPreferredSize(new Dimension(115, 36));

		btnEditar.addActionListener(e -> editar());
		btnEliminar.addActionListener(e -> eliminar());

		inferior.add(btnEditar);
		inferior.add(btnEliminar);

		panel.add(inferior, BorderLayout.SOUTH);

		// Selección de registros.

		table.getSelectionModel().addListSelectionListener(e -> {

			if (cargando || e.getValueIsAdjusting() || table.getSelectedRow() < 0) {
				return;
			}

			int fila = table.getSelectedRow();

			int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

			seleccionada = dao.recuperarPorId(id);

			if (seleccionada != null) {

				txtNombre.setText(seleccionada.getNombre_categoria());

				modoSeleccion();
			}
		});

		// Búsqueda automática.

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

		return panel;
	}

	// ====================== MODOS DE LA PANTALLA ======================

	private void modoNuevo() {

		seleccionada = null;

		txtNombre.setText("");

		table.clearSelection();

		txtNombre.setEnabled(true);

		btnGuardar.setEnabled(true);
		btnCancelar.setEnabled(true);

		btnEditar.setEnabled(false);
		btnEliminar.setEnabled(false);

		txtNombre.requestFocusInWindow();
	}

	private void modoSeleccion() {

		txtNombre.setEnabled(false);

		btnGuardar.setEnabled(false);
		btnCancelar.setEnabled(true);

		btnEditar.setEnabled(true);
		btnEliminar.setEnabled(true);
	}

	private void modoEdicion() {

		txtNombre.setEnabled(true);

		btnGuardar.setEnabled(true);
		btnEditar.setEnabled(false);

		txtNombre.requestFocusInWindow();
		txtNombre.selectAll();
	}

	// ====================== CARGAR TABLA ======================

	private void cargarTabla() {

		String filtro = txtBuscar.getText().trim().toLowerCase();

		List<Categoria_Producto> lista = dao.recuperarPorFiltro("");

		cargando = true;

		try {

			modelo.setRowCount(0);

			int filaSeleccionada = -1;

			for (Categoria_Producto c : lista) {

				String nombre = c.getNombre_categoria() == null ? "" : c.getNombre_categoria();

				if (filtro.isEmpty() || nombre.toLowerCase().contains(filtro)) {

					modelo.addRow(new Object[] { c.getId_categoria_producto(), nombre });

					if (seleccionada != null
							&& c.getId_categoria_producto() == seleccionada.getId_categoria_producto()) {

						filaSeleccionada = modelo.getRowCount() - 1;
					}
				}
			}

			if (filaSeleccionada >= 0) {

				table.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
			}

		} finally {

			cargando = false;
		}
	}

	// ====================== EDITAR ======================

	private void editar() {

		if (seleccionada == null) {

			JOptionPane.showMessageDialog(this, "Seleccione una categoría de la tabla.");

			return;
		}

		modoEdicion();
	}

	// ====================== GUARDAR ======================

	private void guardar() {

		if (!btnGuardar.isEnabled()) {
			return;
		}

		String nombre = txtNombre.getText().trim();

		if (nombre.isEmpty()) {

			JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.", "Validación",
					JOptionPane.WARNING_MESSAGE);

			txtNombre.requestFocusInWindow();

			return;
		}

		try {

			Categoria_Producto c = seleccionada == null ? new Categoria_Producto() : seleccionada;

			c.setNombre_categoria(nombre);

			dao.guardar(c);

			JOptionPane.showMessageDialog(this, "Categoría guardada correctamente.", "PretaSystem",
					JOptionPane.INFORMATION_MESSAGE);

			modoNuevo();

			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Error al guardar la categoría: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ====================== ELIMINAR ======================

	private void eliminar() {

		if (seleccionada == null) {

			JOptionPane.showMessageDialog(this, "Seleccione una categoría de la tabla.");

			return;
		}

		int opcion = JOptionPane.showConfirmDialog(this,
				"¿Está seguro de eliminar la categoría \"" + seleccionada.getNombre_categoria() + "\"?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (opcion != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			dao.eliminar(seleccionada);

			JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.", "PretaSystem",
					JOptionPane.INFORMATION_MESSAGE);

			modoNuevo();

			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this,
					"No se pudo eliminar la categoría.\n" + "Puede estar siendo utilizada por algún producto.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ====================== FONDO DEGRADADO ======================

	private static class FondoPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);

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

	// ====================== PANEL BLANCO REDONDEADO ======================

	private static class PanelTarjeta extends JPanel {

		private static final long serialVersionUID = 1L;

		PanelTarjeta() {
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			// Sombra.

			g2.setColor(new Color(173, 20, 87, 30));

			g2.fillRoundRect(5, 7, w - 10, h - 12, 30, 30);

			// Fondo blanco.

			g2.setColor(new Color(255, 255, 255, 248));

			g2.fillRoundRect(0, 0, w - 10, h - 12, 30, 30);

			g2.dispose();

			super.paintComponent(g);
		}
	}

	// ====================== BOTÓN ROSA ======================

	private static class BotonRosa extends JButton {

		private static final long serialVersionUID = 1L;

		BotonRosa(String texto) {

			super(texto);

			setFont(new Font("Segoe UI", Font.BOLD, 13));

			setForeground(Color.WHITE);

			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (!isEnabled()) {
				g2.setColor(new Color(220, 190, 202));
			} else if (getModel().isRollover()) {
				g2.setColor(ROSA_OSCURO);
			} else {
				g2.setColor(ROSA_FUERTE);
			}

			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

			g2.dispose();

			super.paintComponent(g);
		}
	}

	// ====================== BOTÓN SECUNDARIO ======================

	private static class BotonSecundario extends JButton {

		private static final long serialVersionUID = 1L;

		BotonSecundario(String texto) {

			super(texto);

			setFont(new Font("Segoe UI", Font.BOLD, 13));

			setForeground(ROSA_OSCURO);

			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(getModel().isRollover() ? ROSA_CLARO : Color.WHITE);

			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

			g2.setColor(ROSA_MEDIO);

			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

			g2.dispose();

			super.paintComponent(g);
		}
	}

	// ====================== BOTÓN ELIMINAR ======================

	private static class BotonEliminar extends JButton {

		private static final long serialVersionUID = 1L;

		private static final Color ROJO = new Color(211, 47, 91);

		BotonEliminar(String texto) {

			super(texto);

			setFont(new Font("Segoe UI", Font.BOLD, 13));

			setForeground(Color.WHITE);

			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(getModel().isRollover() ? new Color(173, 20, 60) : ROJO);

			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

			g2.dispose();

			super.paintComponent(g);
		}
	}
}

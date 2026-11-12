
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
import java.sql.SQLException;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import facitec.edu.py.pretasystem_dao.Proveedor_Dao;
import facitec.edu.py.pretasystem_entidades.Proveedor;

public class ProveedorVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	// ====================== COLORES ======================

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);

	private static final Color ROSA_OSCURO = new Color(173, 20, 87);

	private static final Color ROSA_MEDIO = new Color(244, 143, 177);

	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private static final Color TEXTO = new Color(90, 55, 70);

	// ====================== COMPONENTES ======================

	private JTextField txtDocumento;
	private JTextField txtNombre;
	private JTextField txtDireccion;
	private JTextField txtTelefono;
	private JTextField txtEmail;
	private JTextField txtBuscar;

	private JButton btnGuardar;
	private JButton btnEditar;
	private JButton btnEliminar;
	private JButton btnCancelar;

	private JTable table;
	private DefaultTableModel modelo;

	private final Proveedor_Dao dao = new Proveedor_Dao();

	private Proveedor seleccionado = null;

	private boolean modoEdicion = false;
	private boolean cargando = false;

	// ====================== INICIO ======================

	public static void main(String[] args) {

		EventQueue.invokeLater(() -> new ProveedorVentana().setVisible(true));
	}

	public ProveedorVentana() {

		setTitle("PretaSystem - Proveedores");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setSize(1100, 680);

		// Deshabilitar maximización.
		setResizable(false);

		setLocationRelativeTo(null);

		FondoPanel fondo = new FondoPanel();

		fondo.setLayout(new BorderLayout());

		setContentPane(fondo);

		fondo.add(crearEncabezado(), BorderLayout.NORTH);

		fondo.add(crearContenido(), BorderLayout.CENTER);

		cargarTabla();

		limpiar();
	}

	// ====================== ENCABEZADO ======================

	private JPanel crearEncabezado() {

		JPanel encabezado = new JPanel(new BorderLayout());

		encabezado.setOpaque(false);

		encabezado.setBorder(BorderFactory.createEmptyBorder(22, 30, 12, 30));

		JPanel textos = new JPanel(new GridLayout(2, 1));

		textos.setOpaque(false);

		JLabel titulo = new JLabel("Proveedores");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));

		titulo.setForeground(ROSA_OSCURO);

		JLabel subtitulo = new JLabel("Administración y registro de proveedores");

		subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		subtitulo.setForeground(new Color(150, 80, 110));

		textos.add(titulo);
		textos.add(subtitulo);

		encabezado.add(textos, BorderLayout.WEST);

		BotonRosa btnSalir = new BotonRosa("Salir");

		btnSalir.setPreferredSize(new Dimension(110, 38));

		btnSalir.addActionListener(e -> salir());

		JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));

		derecha.setOpaque(false);

		derecha.add(btnSalir);

		encabezado.add(derecha, BorderLayout.EAST);

		return encabezado;
	}

	// ====================== CONTENIDO ======================

	private JPanel crearContenido() {

		JPanel contenedor = new JPanel(new BorderLayout());

		contenedor.setOpaque(false);

		contenedor.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

		PanelTarjeta tarjeta = new PanelTarjeta();

		tarjeta.setLayout(new BorderLayout(18, 0));

		tarjeta.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

		tarjeta.add(crearFormulario(), BorderLayout.WEST);

		tarjeta.add(crearListado(), BorderLayout.CENTER);

		contenedor.add(tarjeta, BorderLayout.CENTER);

		return contenedor;
	}

	// ====================== FORMULARIO ======================

	private JPanel crearFormulario() {

		JPanel panel = new JPanel(new BorderLayout(0, 15));

		panel.setOpaque(false);

		panel.setPreferredSize(new Dimension(320, 0));

		JLabel titulo = new JLabel("Datos del proveedor");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));

		titulo.setForeground(ROSA_OSCURO);

		panel.add(titulo, BorderLayout.NORTH);

		JPanel campos = new JPanel(new GridLayout(5, 1, 0, 12));

		campos.setOpaque(false);

		txtDocumento = crearCampo(campos, "Documento (RUC/CI):");

		txtNombre = crearCampo(campos, "Nombre:");

		txtDireccion = crearCampo(campos, "Dirección:");

		txtTelefono = crearCampo(campos, "Teléfono:");

		txtEmail = crearCampo(campos, "Correo electrónico:");

		panel.add(campos, BorderLayout.CENTER);

		// Botones.

		JPanel botones = new JPanel(new GridLayout(3, 2, 10, 10));

		botones.setOpaque(false);

		btnGuardar = new BotonRosa("Guardar");

		btnEditar = new BotonSecundario("Editar");

		btnEliminar = new BotonEliminar("Eliminar");

		btnCancelar = new BotonSecundario("Limpiar");

		BotonSecundario btnSalir = new BotonSecundario("Salir");

		btnGuardar.addActionListener(e -> guardar());

		btnEditar.addActionListener(e -> editar());

		btnEliminar.addActionListener(e -> eliminar());

		btnCancelar.addActionListener(e -> limpiar());

		btnSalir.addActionListener(e -> salir());

		botones.add(btnGuardar);
		botones.add(btnEditar);

		botones.add(btnEliminar);
		botones.add(btnCancelar);

		botones.add(btnSalir);

		panel.add(botones, BorderLayout.SOUTH);

		return panel;
	}

	// ====================== CREAR CAMPOS ======================

	private JTextField crearCampo(JPanel panel, String etiqueta) {

		JPanel grupo = new JPanel(new BorderLayout(0, 5));

		grupo.setOpaque(false);

		JLabel lbl = new JLabel(etiqueta);

		lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

		lbl.setForeground(TEXTO);

		JTextField campo = new JTextField();

		campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		campo.setPreferredSize(new Dimension(280, 36));

		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO, 1),
				BorderFactory.createEmptyBorder(5, 9, 5, 9)));

		grupo.add(lbl, BorderLayout.NORTH);

		grupo.add(campo, BorderLayout.CENTER);

		panel.add(grupo);

		return campo;
	}

	// ====================== LISTADO ======================

	private JPanel crearListado() {

		JPanel panel = new JPanel(new BorderLayout(0, 13));

		panel.setOpaque(false);

		// Encabezado.

		JPanel superior = new JPanel(new BorderLayout(0, 10));

		superior.setOpaque(false);

		JLabel titulo = new JLabel("Listado de proveedores");

		titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));

		titulo.setForeground(ROSA_OSCURO);

		superior.add(titulo, BorderLayout.NORTH);

		// Buscador.

		JPanel busqueda = new JPanel(new BorderLayout(8, 0));

		busqueda.setOpaque(false);

		JLabel lblBuscar = new JLabel("Buscar:");

		lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));

		lblBuscar.setForeground(TEXTO);

		txtBuscar = new JTextField();

		txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		txtBuscar.setPreferredSize(new Dimension(250, 35));

		txtBuscar.setToolTipText("Buscar proveedor por documento, nombre u otros datos");

		txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		busqueda.add(lblBuscar, BorderLayout.WEST);

		busqueda.add(txtBuscar, BorderLayout.CENTER);

		superior.add(busqueda, BorderLayout.CENTER);

		panel.add(superior, BorderLayout.NORTH);

		// Modelo.

		modelo = new DefaultTableModel(new Object[] { "ID", "Documento", "Nombre", "Dirección", "Teléfono", "Email" },
				0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {

				return false;
			}
		};

		table = new JTable(modelo);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.setRowHeight(34);

		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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

		encabezado.setFont(new Font("Segoe UI", Font.BOLD, 12));

		encabezado.setBackground(ROSA_FUERTE);

		encabezado.setForeground(Color.WHITE);

		encabezado.setReorderingAllowed(false);

		// Anchos de columnas.

		int[] anchos = { 40, 105, 135, 150, 95, 135 };

		for (int i = 0; i < anchos.length; i++) {

			table.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
		}

		DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();

		centrado.setHorizontalAlignment(SwingConstants.CENTER);

		table.getColumnModel().getColumn(0).setCellRenderer(centrado);

		JScrollPane scroll = new JScrollPane(table);

		scroll.setBorder(BorderFactory.createLineBorder(new Color(245, 210, 225)));

		scroll.getViewport().setBackground(Color.WHITE);

		panel.add(scroll, BorderLayout.CENTER);

		// Eventos de selección.

		table.getSelectionModel().addListSelectionListener(e -> {

			if (cargando || e.getValueIsAdjusting()) {
				return;
			}

			seleccionarRegistro();
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

	// ====================== SELECCIONAR REGISTRO ======================

	private void seleccionarRegistro() {

		int fila = table.getSelectedRow();

		if (fila < 0) {

			seleccionado = null;

			return;
		}

		int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

		seleccionado = dao.recuperarPorId(id);

		modoEdicion = false;
	}

	// ====================== CARGAR TABLA ======================

	private void cargarTabla() {

		if (modelo == null) {
			return;
		}

		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();

		List<Proveedor> lista = dao.recuperarPorFiltro(filtro);

		cargando = true;

		try {

			modelo.setRowCount(0);

			int filaSeleccionada = -1;

			for (Proveedor p : lista) {

				modelo.addRow(new Object[] { p.getId_proveedores(), p.getDocumento(), p.getNombre(), p.getDireccion(),
						p.getTelefono(), p.getEmail() });

				if (seleccionado != null && seleccionado.getId_proveedores() == p.getId_proveedores()) {

					filaSeleccionada = modelo.getRowCount() - 1;
				}
			}

			if (filaSeleccionada >= 0) {

				table.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
			}

		} finally {

			cargando = false;
		}
	}

	// ====================== GUARDAR ======================

	private void guardar() {

		String doc = txtDocumento.getText().trim();

		String nombre = txtNombre.getText().trim();

		String dir = txtDireccion.getText().trim();

		String tel = txtTelefono.getText().trim();

		String email = txtEmail.getText().trim();

		if (doc.isEmpty() || nombre.isEmpty() || dir.isEmpty() || tel.isEmpty() || email.isEmpty()) {

			JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación",
					JOptionPane.WARNING_MESSAGE);

			return;
		}

		if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {

			JOptionPane.showMessageDialog(this, "El email no es válido.", "Validación", JOptionPane.WARNING_MESSAGE);

			return;
		}

		try {

			Integer idExcluir = null;

			if (modoEdicion) {

				if (seleccionado == null) {

					JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar.", "Atención",
							JOptionPane.WARNING_MESSAGE);

					return;
				}

				idExcluir = seleccionado.getId_proveedores();
			}

			if (dao.existeDocumento(doc, idExcluir)) {

				JOptionPane.showMessageDialog(this, "Ya existe un proveedor registrado con ese CI/RUC.",
						"Documento duplicado", JOptionPane.WARNING_MESSAGE);

				txtDocumento.requestFocus();

				txtDocumento.selectAll();

				return;
			}

			if (modoEdicion) {

				seleccionado.setDocumento(doc);

				seleccionado.setNombre(nombre);

				seleccionado.setDireccion(dir);

				seleccionado.setTelefono(tel);

				seleccionado.setEmail(email);

				dao.guardar(seleccionado);

				JOptionPane.showMessageDialog(this, "Proveedor editado correctamente.", "Operación exitosa",
						JOptionPane.INFORMATION_MESSAGE);

			} else {

				Proveedor p = new Proveedor();

				p.setDocumento(doc);

				p.setNombre(nombre);

				p.setDireccion(dir);

				p.setTelefono(tel);

				p.setEmail(email);

				dao.guardar(p);

				JOptionPane.showMessageDialog(this, "Proveedor guardado correctamente.", "Operación exitosa",
						JOptionPane.INFORMATION_MESSAGE);
			}

			limpiar();

			cargarTabla();

		} catch (Exception ex) {

			if (esErrorDeDocumentoDuplicado(ex)) {

				JOptionPane.showMessageDialog(this,
						"Ya existe un proveedor registrado con ese CI/RUC.\n" + "Ingrese un documento diferente.",
						"Documento duplicado", JOptionPane.WARNING_MESSAGE);

				txtDocumento.requestFocus();

				txtDocumento.selectAll();

			} else {

				JOptionPane.showMessageDialog(this,
						"No se pudo guardar el proveedor. " + "Verifique los datos e intente nuevamente.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// ====================== VALIDAR DOCUMENTO DUPLICADO ======================

	private boolean esErrorDeDocumentoDuplicado(Throwable ex) {

		Throwable causa = ex;

		while (causa != null) {

			if (causa instanceof SQLException) {

				String estado = ((SQLException) causa).getSQLState();

				if ("23505".equals(estado) || "23000".equals(estado)) {

					return true;
				}
			}

			String nombreClase = causa.getClass().getSimpleName();

			if ("ConstraintViolationException".equals(nombreClase)) {

				return true;
			}

			causa = causa.getCause();
		}

		return false;
	}

	// ====================== EDITAR ======================

	private void editar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.", "Atención",
					JOptionPane.WARNING_MESSAGE);

			return;
		}

		txtDocumento.setText(seleccionado.getDocumento());

		txtNombre.setText(seleccionado.getNombre());

		txtDireccion.setText(seleccionado.getDireccion());

		txtTelefono.setText(seleccionado.getTelefono());

		txtEmail.setText(seleccionado.getEmail());

		modoEdicion = true;

		txtDocumento.requestFocus();

		JOptionPane.showMessageDialog(this, "Puede modificar los datos del proveedor seleccionado.", "Modo edición",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// ====================== ELIMINAR ======================

	private void eliminar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.", "Atención",
					JOptionPane.WARNING_MESSAGE);

			return;
		}

		int op = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar \"" + seleccionado.getNombre() + "\"?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			dao.eliminar(seleccionado);

			JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.", "Operación exitosa",
					JOptionPane.INFORMATION_MESSAGE);

			limpiar();

			cargarTabla();

		} catch (Exception ex) {

			JOptionPane.showMessageDialog(this,
					"No se pudo eliminar el proveedor. " + "Es posible que esté relacionado con otros registros.",
					"Error al eliminar", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ====================== LIMPIAR ======================

	private void limpiar() {

		seleccionado = null;

		modoEdicion = false;

		txtDocumento.setText("");

		txtNombre.setText("");

		txtDireccion.setText("");

		txtTelefono.setText("");

		txtEmail.setText("");

		table.clearSelection();

		txtDocumento.requestFocusInWindow();
	}

	// ====================== SALIR ======================

	private void salir() {

		dispose();
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

	// ====================== PANEL BLANCO ======================

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

			g2.setColor(new Color(173, 20, 87, 30));

			g2.fillRoundRect(5, 7, w - 10, h - 12, 30, 30);

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

			setFont(new Font("Segoe UI", Font.BOLD, 12));

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

			g2.setColor(!isEnabled() ? new Color(220, 190, 202) : getModel().isRollover() ? ROSA_OSCURO : ROSA_FUERTE);

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

			setFont(new Font("Segoe UI", Font.BOLD, 12));

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

			setFont(new Font("Segoe UI", Font.BOLD, 12));

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

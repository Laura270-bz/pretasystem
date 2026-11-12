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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_util.Iconos;

/**
 * ABM de Materia Prima con el mismo diseño de la pantalla principal.
 */
public class MateriaPrimaVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
	private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

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
		setIconImage(Iconos.imagen("ovillo", 64, ROSA_FUERTE));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Sin opción de maximizar ni de cambiar el tamaño.
		setResizable(false);

		setSize(1024, 680);
		setLocationRelativeTo(null);

		setContentPane(new FondoPanel());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
		getContentPane().add(crearCuerpo(), BorderLayout.CENTER);

		cargarTabla();
	}

	// ====================== DISEÑO ======================

	private JPanel crearEncabezado() {

		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(20, 30, 5, 30));

		JLabel titulo = new JLabel("Materia Prima");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titulo.setForeground(ROSA_OSCURO);

		JLabel sub = new JLabel("Registro y control de stock de materiales");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		sub.setForeground(new Color(150, 80, 110));

		p.add(titulo);
		p.add(sub);

		return p;
	}

	private JPanel crearCuerpo() {

		JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
		cuerpo.setOpaque(false);
		cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

		cuerpo.add(crearFormulario(), BorderLayout.WEST);
		cuerpo.add(crearPanelTabla(), BorderLayout.CENTER);

		return cuerpo;
	}

	private JPanel crearFormulario() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new GridBagLayout());
		card.setPreferredSize(new Dimension(300, 0));
		card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;

		int fila = 0;

		JLabel lblForm = new JLabel("Datos del registro");
		lblForm.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblForm.setForeground(ROSA_OSCURO);
		c.gridy = fila++;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 14, 0);
		card.add(lblForm, c);

		// Nombre
		c.gridy = fila++;
		c.insets = new Insets(0, 0, 4, 0);
		card.add(etiqueta("Nombre"), c);

		txtNombre = new JTextField();
		estilizarCampo(txtNombre);
		c.gridy = fila++;
		c.insets = new Insets(0, 0, 14, 0);
		card.add(txtNombre, c);

		// Stock actual + unidad
		c.gridy = fila++;
		c.insets = new Insets(0, 0, 4, 0);
		card.add(etiqueta("Stock actual y unidad"), c);

		spnStockActual = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
		FormatoUtil.aplicarMiles(spnStockActual);
		estilizarSpinner(spnStockActual);

		cmbUnidad = new JComboBox<>(new String[] { "Kg", "g", "L", "ml", "Unid." });
		cmbUnidad.setFont(FUENTE_CAMPO);
		cmbUnidad.setBackground(Color.WHITE);
		cmbUnidad.setPreferredSize(new Dimension(80, 32));

		c.gridy = fila++;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 14, 8);
		card.add(spnStockActual, c);

		c.gridx = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 14, 0);
		card.add(cmbUnidad, c);

		// Stock mínimo
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.gridy = fila++;
		c.insets = new Insets(0, 0, 4, 0);
		card.add(etiqueta("Stock mínimo"), c);

		spnStockMinimo = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
		FormatoUtil.aplicarMiles(spnStockMinimo);
		estilizarSpinner(spnStockMinimo);

		c.gridy = fila++;
		c.insets = new Insets(0, 0, 22, 0);
		card.add(spnStockMinimo, c);

		// Botones principales
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

		c.gridy = fila++;
		c.insets = new Insets(0, 0, 10, 0);
		card.add(botones, c);

		BotonRosa btnSalir = new BotonRosa("Salir", false);
		btnSalir.addActionListener(e -> salir());
		c.gridy = fila++;
		c.insets = new Insets(0, 0, 0, 0);
		card.add(btnSalir, c);

		// Empuja todo hacia arriba.
		c.gridy = fila;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JPanel relleno = new JPanel();
		relleno.setOpaque(false);
		card.add(relleno, c);

		return card;
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

		JLabel lblLupa = new JLabel("\uD83D\uDD0D");
		lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
		buscador.add(lblLupa, BorderLayout.EAST);

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

		card.add(buscador, BorderLayout.NORTH);

		// ---------- TABLA ----------

		modeloTabla = new DefaultTableModel(new Object[] { "ID", "Nombre", "Stock Actual", "Unidad", "Stock Mínimo" },
				0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(modeloTabla);
		table.setFont(FUENTE_CAMPO);
		table.setRowHeight(28);
		table.setShowVerticalLines(false);
		table.setGridColor(ROSA_CLARO);
		table.setSelectionBackground(ROSA_MEDIO);
		table.setSelectionForeground(Color.WHITE);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Encabezado de la tabla en rosa.
		DefaultTableCellRenderer header = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r,
					int col) {

				JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, col);

				l.setBackground(ROSA_FUERTE);
				l.setForeground(Color.WHITE);
				l.setFont(new Font("Segoe UI", Font.BOLD, 13));
				l.setHorizontalAlignment(SwingConstants.CENTER);
				l.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
				l.setOpaque(true);

				return l;
			}
		};

		table.getTableHeader().setDefaultRenderer(header);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(0, 34));

		table.getColumnModel().getColumn(2).setCellRenderer(FormatoUtil.rendererMiles());
		table.getColumnModel().getColumn(4).setCellRenderer(FormatoUtil.rendererMiles());

		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(220);
		table.getColumnModel().getColumn(2).setPreferredWidth(110);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(110);

		// Al seleccionar una fila, solamente recupera el registro.
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				seleccionarRegistro();
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
		scroll.getViewport().setBackground(Color.WHITE);

		card.add(scroll, BorderLayout.CENTER);

		return card;
	}

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

	// ====================== LÓGICA ======================

	/**
	 * Carga las materias primas en la tabla. Utiliza el filtro escrito en el
	 * buscador.
	 */
	private void cargarTabla() {

		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();

		List<Materia_Prima> lista = dao.recuperarPorFiltro(filtro);

		modeloTabla.setRowCount(0);

		for (Materia_Prima m : lista) {

			modeloTabla.addRow(new Object[] { m.getId_materia_prima(), m.getNombre(), m.getStock_actual(),
					m.getUnidad_medida(), m.getStock_minimo() });
		}
	}

	/**
	 * Recupera el registro seleccionado en la tabla, pero no carga sus datos en el
	 * formulario.
	 */
	private void seleccionarRegistro() {

		int fila = table.getSelectedRow();

		if (fila < 0) {
			materiaSeleccionada = null;
			modoEdicion = false;
			return;
		}

		Integer id = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();

		materiaSeleccionada = dao.recuperarPorId(id);

		// Todavía no se permite guardar como edición.
		modoEdicion = false;
	}

	/**
	 * Registra una nueva materia prima o actualiza el registro seleccionado cuando
	 * está en modo edición.
	 */
	private void guardar() {

		String nombre = txtNombre.getText().trim();

		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
			txtNombre.requestFocus();
			return;
		}

		try {

			if (modoEdicion) {

				if (materiaSeleccionada == null) {
					JOptionPane.showMessageDialog(this, "Seleccione una materia prima para editar");
					return;
				}

				materiaSeleccionada.setNombre(nombre);
				materiaSeleccionada.setStock_actual(((Number) spnStockActual.getValue()).intValue());
				materiaSeleccionada.setUnidad_medida(cmbUnidad.getSelectedItem().toString());
				materiaSeleccionada.setStock_minimo(((Number) spnStockMinimo.getValue()).intValue());

				dao.guardar(materiaSeleccionada);

				JOptionPane.showMessageDialog(this, "Materia prima editada correctamente");

			} else {

				Materia_Prima m = new Materia_Prima();

				m.setNombre(nombre);
				m.setStock_actual(((Number) spnStockActual.getValue()).intValue());
				m.setUnidad_medida(cmbUnidad.getSelectedItem().toString());
				m.setStock_minimo(((Number) spnStockMinimo.getValue()).intValue());

				dao.guardar(m);

				JOptionPane.showMessageDialog(this, "Materia prima guardada correctamente");
			}

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
		}
	}

	/**
	 * Activa el modo edición y carga los datos de la materia prima seleccionada en
	 * el formulario.
	 */
	private void editar() {

		if (materiaSeleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una materia prima de la tabla");
			return;
		}

		txtNombre.setText(materiaSeleccionada.getNombre());
		spnStockActual.setValue(materiaSeleccionada.getStock_actual());
		cmbUnidad.setSelectedItem(materiaSeleccionada.getUnidad_medida());
		spnStockMinimo.setValue(materiaSeleccionada.getStock_minimo());

		modoEdicion = true;

		JOptionPane.showMessageDialog(this, "Puede modificar los datos del registro seleccionado");
	}

	/**
	 * Elimina la materia prima seleccionada.
	 */
	private void eliminar() {

		if (materiaSeleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla");
			return;
		}

		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + materiaSeleccionada.getNombre() + "\"?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			dao.eliminar(materiaSeleccionada);

			JOptionPane.showMessageDialog(this, "Materia prima eliminada correctamente");

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
		}
	}

	/**
	 * Limpia el formulario, elimina la selección y desactiva el modo edición.
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
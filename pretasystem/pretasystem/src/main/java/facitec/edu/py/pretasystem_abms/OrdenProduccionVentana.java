package facitec.edu.py.pretasystem_abms;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_util.Iconos;
import facitec.edu.py.pretasystem_util.SesionUsuario;

/**
 * TRANSACCION: Orden de Produccion + Consumo de Materia Prima (descuenta
 * stock). Mismo diseño que la pantalla principal.
 */
public class OrdenProduccionVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
	private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

	private JSpinner spnFechaInicio;
	private JSpinner spnCantidadEstimada;
	private JComboBox<String> cmbEtapa;
	private JComboBox<Materia_Prima> cmbMateria;
	private JSpinner spnCantidad;
	private JTable table;
	private DefaultTableModel modeloTabla;

	private final Materia_Prima_Dao materiaDao = new Materia_Prima_Dao();
	private final Orden_Produccion_Dao ordenDao = new Orden_Produccion_Dao();
	private final List<Consumo_Materia_Prima> detalle = new ArrayList<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new OrdenProduccionVentana().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public OrdenProduccionVentana() {

		setTitle("Orden de Producción - Consumo de Materia Prima");
		setIconImage(Iconos.imagen("orden", 64, ROSA_FUERTE));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Sin opción de maximizar ni de cambiar el tamaño.
		setResizable(false);

		setSize(900, 660);
		setLocationRelativeTo(null);

		setContentPane(new FondoPanel());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
		getContentPane().add(crearCuerpo(), BorderLayout.CENTER);

		cargarCombos();
	}

	// ====================== DISEÑO ======================

	private JPanel crearEncabezado() {

		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(20, 30, 5, 30));

		JLabel titulo = new JLabel("Orden de Producción");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titulo.setForeground(ROSA_OSCURO);

		JLabel sub = new JLabel("Registro de la orden y consumo de materia prima");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		sub.setForeground(new Color(150, 80, 110));

		p.add(titulo);
		p.add(sub);

		return p;
	}

	private JPanel crearCuerpo() {

		JPanel cuerpo = new JPanel(new BorderLayout(0, 15));
		cuerpo.setOpaque(false);
		cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 30, 25, 30));

		cuerpo.add(crearPanelOrden(), BorderLayout.NORTH);
		cuerpo.add(crearPanelDetalle(), BorderLayout.CENTER);
		cuerpo.add(crearBotones(), BorderLayout.SOUTH);

		return cuerpo;
	}

	/** Tarjeta con los datos de la orden: fecha, cantidad estimada y etapa. */
	private JPanel crearPanelOrden() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new GridBagLayout());
		card.setBorder(BorderFactory.createEmptyBorder(16, 22, 18, 22));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;

		JLabel lblTitulo = new JLabel("Datos de la orden");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblTitulo.setForeground(ROSA_OSCURO);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 12, 0);
		card.add(lblTitulo, c);

		c.gridwidth = 1;

		// Etiquetas
		c.gridy = 1;
		c.insets = new Insets(0, 0, 4, 15);
		c.gridx = 0;
		card.add(etiqueta("Fecha de inicio"), c);
		c.gridx = 1;
		card.add(etiqueta("Cantidad estimada"), c);
		c.gridx = 2;
		c.insets = new Insets(0, 0, 4, 0);
		card.add(etiqueta("Etapa"), c);

		// Campos
		spnFechaInicio = new JSpinner(new SpinnerDateModel());
		spnFechaInicio.setEditor(new JSpinner.DateEditor(spnFechaInicio, "dd/MM/yyyy"));
		estilizarSpinner(spnFechaInicio);

		spnCantidadEstimada = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		FormatoUtil.aplicarMiles(spnCantidadEstimada);
		estilizarSpinner(spnCantidadEstimada);

		cmbEtapa = new JComboBox<>(new String[] { "Corte", "Limpieza", "Ovillado", "Bobinado", "Sellado" });
		estilizarCombo(cmbEtapa);

		c.gridy = 2;
		c.gridx = 0;
		c.insets = new Insets(0, 0, 0, 15);
		card.add(spnFechaInicio, c);
		c.gridx = 1;
		card.add(spnCantidadEstimada, c);
		c.gridx = 2;
		c.insets = new Insets(0, 0, 0, 0);
		card.add(cmbEtapa, c);

		return card;
	}

	/** Tarjeta con el selector de materia prima y la tabla del detalle. */
	private JPanel crearPanelDetalle() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new BorderLayout(0, 12));
		card.setBorder(BorderFactory.createEmptyBorder(16, 22, 20, 22));

		// ---------- ENCABEZADO + SELECTOR ----------

		JPanel arriba = new JPanel(new BorderLayout(0, 10));
		arriba.setOpaque(false);

		JLabel lblTitulo = new JLabel("Materia prima a consumir");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblTitulo.setForeground(ROSA_OSCURO);
		arriba.add(lblTitulo, BorderLayout.NORTH);

		JPanel fila = new JPanel(new GridBagLayout());
		fila.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;

		cmbMateria = new JComboBox<>();
		estilizarCombo(cmbMateria);
		cmbMateria.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel,
					boolean focus) {
				super.getListCellRendererComponent(list, value, index, sel, focus);
				if (value instanceof Materia_Prima) {
					Materia_Prima m = (Materia_Prima) value;
					setText(m.getNombre() + "  (stock: " + FormatoUtil.miles(m.getStock_actual()) + " "
							+ m.getUnidad_medida() + ")");
				}
				return this;
			}
		});

		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 15);
		fila.add(cmbMateria, c);

		JLabel lblCant = etiqueta("Cantidad:");
		c.gridx = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 8);
		fila.add(lblCant, c);

		spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		FormatoUtil.aplicarMiles(spnCantidad);
		estilizarSpinner(spnCantidad);
		spnCantidad.setPreferredSize(new Dimension(110, 32));
		c.gridx = 2;
		c.insets = new Insets(0, 0, 0, 15);
		fila.add(spnCantidad, c);

		BotonRosa btnAgregar = new BotonRosa("Agregar", true);
		btnAgregar.setPreferredSize(new Dimension(95, 34));
		btnAgregar.addActionListener(e -> agregarDetalle());
		c.gridx = 3;
		c.insets = new Insets(0, 0, 0, 8);
		fila.add(btnAgregar, c);

		BotonRosa btnQuitar = new BotonRosa("Quitar", false);
		btnQuitar.setPreferredSize(new Dimension(95, 34));
		btnQuitar.addActionListener(e -> quitarDetalle());
		c.gridx = 4;
		c.insets = new Insets(0, 0, 0, 0);
		fila.add(btnQuitar, c);

		arriba.add(fila, BorderLayout.CENTER);
		card.add(arriba, BorderLayout.NORTH);

		// ---------- TABLA ----------

		modeloTabla = new DefaultTableModel(new Object[] { "Materia Prima", "Cantidad", "Unidad" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
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

		table.getColumnModel().getColumn(1).setCellRenderer(FormatoUtil.rendererMiles());
		table.getColumnModel().getColumn(0).setPreferredWidth(350);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
		scroll.getViewport().setBackground(Color.WHITE);

		card.add(scroll, BorderLayout.CENTER);

		return card;
	}

	private JPanel crearBotones() {

		JPanel barra = new JPanel(new BorderLayout());
		barra.setOpaque(false);

		JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		izquierda.setOpaque(false);

		BotonRosa btnConfirmar = new BotonRosa("Confirmar orden", true);
		btnConfirmar.setPreferredSize(new Dimension(180, 38));
		btnConfirmar.addActionListener(e -> confirmar());
		izquierda.add(btnConfirmar);

		BotonRosa btnCancelar = new BotonRosa("Cancelar", false);
		btnCancelar.setPreferredSize(new Dimension(110, 38));
		btnCancelar.addActionListener(e -> limpiar());
		izquierda.add(btnCancelar);

		JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		derecha.setOpaque(false);

		BotonRosa btnVerOrdenes = new BotonRosa("Ver órdenes", true);
		btnVerOrdenes.setPreferredSize(new Dimension(150, 38));
		btnVerOrdenes.addActionListener(e -> new OrdenesRegistradasVentana().setVisible(true));
		derecha.add(btnVerOrdenes);

		BotonRosa btnSalir = new BotonRosa("Salir", false);
		btnSalir.setPreferredSize(new Dimension(110, 38));
		btnSalir.addActionListener(e -> salir());
		derecha.add(btnSalir);

		barra.add(izquierda, BorderLayout.WEST);
		barra.add(derecha, BorderLayout.EAST);

		return barra;
	}

	private JLabel etiqueta(String texto) {

		JLabel l = new JLabel(texto);
		l.setFont(FUENTE_LABEL);
		l.setForeground(ROSA_OSCURO);

		return l;
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

	// ====================== LÓGICA ======================

	private void cargarCombos() {
		List<Materia_Prima> materias = materiaDao.recuperarPorFiltro("");
		cmbMateria.setModel(new DefaultComboBoxModel<>(materias.toArray(new Materia_Prima[0])));
	}

	private void agregarDetalle() {
		Materia_Prima mp = (Materia_Prima) cmbMateria.getSelectedItem();
		if (mp == null) {
			JOptionPane.showMessageDialog(this, "No hay materias primas cargadas");
			return;
		}
		int cantidad = ((Number) spnCantidad.getValue()).intValue();

		Consumo_Materia_Prima existente = null;
		for (Consumo_Materia_Prima c : detalle) {
			if (c.getMateria_prima().getId_materia_prima() == mp.getId_materia_prima()) {
				existente = c;
				break;
			}
		}
		int yaAgregado = existente == null ? 0 : existente.getCantidad_usada();

		if (yaAgregado + cantidad > mp.getStock_actual()) {
			JOptionPane.showMessageDialog(this, "Stock insuficiente de " + mp.getNombre() + ". Disponible: "
					+ FormatoUtil.miles(mp.getStock_actual()) + " " + mp.getUnidad_medida());
			return;
		}

		if (existente != null) {
			existente.setCantidad_usada(yaAgregado + cantidad);
		} else {
			Consumo_Materia_Prima c = new Consumo_Materia_Prima();
			c.setMateria_prima(mp);
			c.setCantidad_usada(cantidad);
			detalle.add(c);
		}
		refrescarTabla();
	}

	private void quitarDetalle() {
		int fila = table.getSelectedRow();
		if (fila < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione una fila para quitar");
			return;
		}
		detalle.remove(fila);
		refrescarTabla();
	}

	private void refrescarTabla() {
		modeloTabla.setRowCount(0);
		for (Consumo_Materia_Prima c : detalle) {
			modeloTabla.addRow(new Object[] { c.getMateria_prima().getNombre(), c.getCantidad_usada(),
					c.getMateria_prima().getUnidad_medida() });
		}
	}

	private void confirmar() {
		if (detalle.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Agregue al menos una materia prima");
			return;
		}
		int op = JOptionPane.showConfirmDialog(this, "¿Confirmar la orden? Se descontará el stock de materia prima.",
				"Confirmar", JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			Orden_Produccion orden = new Orden_Produccion();
			orden.setFecha_inicio(new Date(((java.util.Date) spnFechaInicio.getValue()).getTime()));
			orden.setCantidad_estimada(((Number) spnCantidadEstimada.getValue()).intValue());
			orden.setEtapa_actual(cmbEtapa.getSelectedItem().toString());
			orden.setCantidad_real(0);
			orden.setUsuario(SesionUsuario.getUsuario());

			List<String> alertas = ordenDao.guardarOrdenConConsumos(orden, new ArrayList<>(detalle));

			String msg = "Orden N° " + orden.getId_orden_produccion() + " registrada.\nPendiente de producir: "
					+ FormatoUtil.miles(orden.getCantidad_estimada())
					+ ".\nLa producción se registra en Producto Terminado.";
			if (!alertas.isEmpty()) {
				msg += "\n\nALERTA - stock minimo:\n- " + String.join("\n- ", alertas);
			}
			JOptionPane.showMessageDialog(this, msg);
			limpiar();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo guardar", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void limpiar() {
		detalle.clear();
		refrescarTabla();
		spnCantidadEstimada.setValue(1);
		spnCantidad.setValue(1);
		cmbEtapa.setSelectedIndex(0);
		cargarCombos();
	}

	// Cierra solo esta ventana; si hay una orden a medias, pregunta antes
	private void salir() {
		if (!detalle.isEmpty()) {
			int op = JOptionPane.showConfirmDialog(this,
					"Hay materias primas cargadas que no se confirmaron. ¿Salir igual?", "Confirmar",
					JOptionPane.YES_NO_OPTION);
			if (op != JOptionPane.YES_OPTION) {
				return;
			}
		}
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
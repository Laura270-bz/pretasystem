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
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_util.Iconos;

/**
 * Listado de ordenes de produccion ya guardadas, con el detalle de materia
 * prima consumida. Mismo diseño que la pantalla principal.
 */
public class OrdenesRegistradasVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
	private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

	private JTextField txtBuscar;
	private JTable tablaOrdenes;
	private JTable tablaDetalle;
	private DefaultTableModel modeloOrdenes;
	private DefaultTableModel modeloDetalle;
	private final Orden_Produccion_Dao dao = new Orden_Produccion_Dao();
	private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new OrdenesRegistradasVentana().setVisible(true));
	}

	public OrdenesRegistradasVentana() {

		setTitle("Órdenes de Producción registradas");
		setIconImage(Iconos.imagen("orden", 64, ROSA_FUERTE));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Sin opción de maximizar ni de cambiar el tamaño.
		setResizable(false);

		setSize(1024, 720);
		setLocationRelativeTo(null);

		setContentPane(new FondoPanel());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
		getContentPane().add(crearCuerpo(), BorderLayout.CENTER);

		cargarOrdenes();
	}

	// ====================== DISEÑO ======================

	private JPanel crearEncabezado() {

		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(20, 30, 5, 30));

		JLabel titulo = new JLabel("Órdenes Registradas");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titulo.setForeground(ROSA_OSCURO);

		JLabel sub = new JLabel("Listado de órdenes de producción y materia prima consumida");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		sub.setForeground(new Color(150, 80, 110));

		p.add(titulo);
		p.add(sub);

		return p;
	}

	private JPanel crearCuerpo() {

		JPanel cuerpo = new JPanel(new GridLayout(2, 1, 0, 15));
		cuerpo.setOpaque(false);
		cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

		cuerpo.add(crearPanelOrdenes());
		cuerpo.add(crearPanelDetalle());

		return cuerpo;
	}

	/** Tarjeta superior: buscador + tabla de órdenes. */
	private JPanel crearPanelOrdenes() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new BorderLayout(0, 12));
		card.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

		// ---------- BUSCADOR ----------

		JPanel buscador = new JPanel(new BorderLayout(10, 0));
		buscador.setOpaque(false);

		JLabel lblBuscar = new JLabel("Buscar por etapa:");
		lblBuscar.setFont(FUENTE_LABEL);
		lblBuscar.setForeground(ROSA_OSCURO);
		buscador.add(lblBuscar, BorderLayout.WEST);

		txtBuscar = new JTextField();
		txtBuscar.setFont(FUENTE_CAMPO);
		txtBuscar.setPreferredSize(new Dimension(100, 32));
		txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ROSA_MEDIO),
				BorderFactory.createEmptyBorder(2, 8, 2, 8)));
		buscador.add(txtBuscar, BorderLayout.CENTER);

		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				cargarOrdenes();
			}

			public void removeUpdate(DocumentEvent e) {
				cargarOrdenes();
			}

			public void changedUpdate(DocumentEvent e) {
				cargarOrdenes();
			}
		});

		BotonRosa btnActualizar = new BotonRosa("Actualizar");
		btnActualizar.setPreferredSize(new Dimension(120, 34));
		btnActualizar.addActionListener(e -> cargarOrdenes());
		buscador.add(btnActualizar, BorderLayout.EAST);

		card.add(buscador, BorderLayout.NORTH);

		// ---------- TABLA DE ORDENES ----------

		modeloOrdenes = new DefaultTableModel(new Object[] { "N° Orden", "Fecha inicio", "Fecha fin", "Etapa",
				"Cant. estimada", "Cant. real", "Supervisor" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tablaOrdenes = new JTable(modeloOrdenes);
		estilizarTabla(tablaOrdenes);
		tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaOrdenes.getColumnModel().getColumn(4).setCellRenderer(FormatoUtil.rendererMiles());
		tablaOrdenes.getColumnModel().getColumn(5).setCellRenderer(FormatoUtil.rendererMiles());
		tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(70);
		tablaOrdenes.getColumnModel().getColumn(3).setPreferredWidth(110);

		tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				cargarDetalle();
			}
		});

		card.add(envolver(tablaOrdenes), BorderLayout.CENTER);

		return card;
	}

	/** Tarjeta inferior: materia prima consumida en la orden seleccionada. */
	private JPanel crearPanelDetalle() {

		TarjetaPanel card = new TarjetaPanel();
		card.setLayout(new BorderLayout(0, 12));
		card.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

		JLabel lblDetalle = new JLabel("Materia prima consumida en la orden seleccionada");
		lblDetalle.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblDetalle.setForeground(ROSA_OSCURO);
		card.add(lblDetalle, BorderLayout.NORTH);

		modeloDetalle = new DefaultTableModel(new Object[] { "Materia Prima", "Cantidad usada", "Unidad" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tablaDetalle = new JTable(modeloDetalle);
		estilizarTabla(tablaDetalle);
		tablaDetalle.getColumnModel().getColumn(1).setCellRenderer(FormatoUtil.rendererMiles());
		tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(350);

		card.add(envolver(tablaDetalle), BorderLayout.CENTER);

		return card;
	}

	private JScrollPane envolver(JTable t) {

		JScrollPane sp = new JScrollPane(t);
		sp.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
		sp.getViewport().setBackground(Color.WHITE);

		return sp;
	}

	/** Aplica a la tabla el estilo rosa (encabezado, selección, filas). */
	private void estilizarTabla(JTable t) {

		t.setFont(FUENTE_CAMPO);
		t.setRowHeight(28);
		t.setShowVerticalLines(false);
		t.setGridColor(ROSA_CLARO);
		t.setSelectionBackground(ROSA_MEDIO);
		t.setSelectionForeground(Color.WHITE);
		t.setFillsViewportHeight(true);

		DefaultTableCellRenderer header = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r,
					int col) {

				JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, sel, foc, r, col);

				l.setBackground(ROSA_FUERTE);
				l.setForeground(Color.WHITE);
				l.setFont(new Font("Segoe UI", Font.BOLD, 13));
				l.setHorizontalAlignment(SwingConstants.CENTER);
				l.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
				l.setOpaque(true);

				return l;
			}
		};

		t.getTableHeader().setDefaultRenderer(header);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setPreferredSize(new Dimension(0, 34));
	}

	// ====================== LÓGICA ======================

	private void cargarOrdenes() {
		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();
		List<Orden_Produccion> lista = dao.recuperarPorFiltro(filtro);
		modeloOrdenes.setRowCount(0);
		modeloDetalle.setRowCount(0);
		for (Orden_Produccion o : lista) {
			modeloOrdenes.addRow(new Object[] { o.getId_orden_produccion(),
					o.getFecha_inicio() == null ? "-" : formatoFecha.format(o.getFecha_inicio()),
					o.getFecha_fin() == null ? "-" : formatoFecha.format(o.getFecha_fin()), o.getEtapa_actual(),
					o.getCantidad_estimada(), o.getCantidad_real() == null ? "-" : o.getCantidad_real(),
					o.getUsuario() == null ? "-" : o.getUsuario().getUsuario() });
		}
	}

	private void cargarDetalle() {
		modeloDetalle.setRowCount(0);
		int fila = tablaOrdenes.getSelectedRow();
		if (fila < 0) {
			return;
		}
		int idOrden = ((Number) modeloOrdenes.getValueAt(fila, 0)).intValue();
		for (Consumo_Materia_Prima c : dao.recuperarConsumos(idOrden)) {
			modeloDetalle.addRow(new Object[] { c.getMateria_prima().getNombre(), c.getCantidad_usada(),
					c.getMateria_prima().getUnidad_medida() });
		}
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

	/** Botón redondeado rosa. */
	private static class BotonRosa extends JButton {

		private static final long serialVersionUID = 1L;

		BotonRosa(String texto) {

			super(texto);

			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setForeground(Color.WHITE);
			setFont(new Font("Segoe UI", Font.BOLD, 13));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(getModel().isRollover() ? ROSA_OSCURO : ROSA_FUERTE);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

			g2.dispose();

			super.paintComponent(g);
		}
	}
}
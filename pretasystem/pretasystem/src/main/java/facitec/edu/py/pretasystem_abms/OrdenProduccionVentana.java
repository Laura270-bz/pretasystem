package facitec.edu.py.pretasystem_abms;

import java.awt.Component;
import java.awt.EventQueue;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_util.SesionUsuario;

/** TRANSACCION: Orden de Produccion + Consumo de Materia Prima (descuenta stock). */
public class OrdenProduccionVentana extends JFrame {

	private static final long serialVersionUID = 1L;

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
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 560);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// ---------- DATOS DE LA ORDEN ----------
		JLabel lblTitulo1 = new JLabel("DATOS DE LA ORDEN");
		lblTitulo1.setBounds(15, 10, 200, 16);
		getContentPane().add(lblTitulo1);

		JLabel lblFecha = new JLabel("Fecha inicio:");
		lblFecha.setBounds(15, 40, 90, 16);
		getContentPane().add(lblFecha);

		spnFechaInicio = new JSpinner(new SpinnerDateModel());
		spnFechaInicio.setEditor(new JSpinner.DateEditor(spnFechaInicio, "dd/MM/yyyy"));
		spnFechaInicio.setBounds(110, 37, 120, 24);
		getContentPane().add(spnFechaInicio);

		JLabel lblEstimada = new JLabel("Cantidad estimada:");
		lblEstimada.setBounds(260, 40, 120, 16);
		getContentPane().add(lblEstimada);

		spnCantidadEstimada = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		FormatoUtil.aplicarMiles(spnCantidadEstimada);
		spnCantidadEstimada.setBounds(385, 37, 100, 24);
		getContentPane().add(spnCantidadEstimada);

		JLabel lblEtapa = new JLabel("Etapa:");
		lblEtapa.setBounds(515, 40, 50, 16);
		getContentPane().add(lblEtapa);

		cmbEtapa = new JComboBox<>(new String[] { "Corte", "Limpieza", "Ovillado", "Bobinado", "Sellado" });
		cmbEtapa.setBounds(565, 37, 200, 24);
		getContentPane().add(cmbEtapa);

		// ---------- DETALLE: MATERIA PRIMA A CONSUMIR ----------
		JLabel lblTitulo2 = new JLabel("MATERIA PRIMA A CONSUMIR");
		lblTitulo2.setBounds(15, 85, 300, 16);
		getContentPane().add(lblTitulo2);

		cmbMateria = new JComboBox<>();
		cmbMateria.setBounds(15, 110, 380, 26);
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
		getContentPane().add(cmbMateria);

		JLabel lblCant = new JLabel("Cantidad:");
		lblCant.setBounds(410, 115, 60, 16);
		getContentPane().add(lblCant);

		spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		FormatoUtil.aplicarMiles(spnCantidad);
		spnCantidad.setBounds(470, 111, 100, 24);
		getContentPane().add(spnCantidad);

		JButton btnAgregar = new JButton("Agregar");
		btnAgregar.setBounds(590, 110, 90, 26);
		btnAgregar.addActionListener(e -> agregarDetalle());
		getContentPane().add(btnAgregar);

		JButton btnQuitar = new JButton("Quitar");
		btnQuitar.setBounds(685, 110, 85, 26);
		btnQuitar.addActionListener(e -> quitarDetalle());
		getContentPane().add(btnQuitar);

		modeloTabla = new DefaultTableModel(new Object[] { "Materia Prima", "Cantidad", "Unidad" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(modeloTabla);
		table.getColumnModel().getColumn(1).setCellRenderer(FormatoUtil.rendererMiles());
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBounds(15, 150, 755, 290);
		getContentPane().add(scroll);

		// ---------- BOTONES ----------
		JButton btnConfirmar = new JButton("Confirmar orden");
		btnConfirmar.setBounds(15, 460, 180, 36);
		btnConfirmar.addActionListener(e -> confirmar());
		getContentPane().add(btnConfirmar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(205, 460, 100, 36);
		btnCancelar.addActionListener(e -> limpiar());
		getContentPane().add(btnCancelar);

		JButton btnVerOrdenes = new JButton("Ver órdenes");
		btnVerOrdenes.setBounds(395, 460, 185, 36);
		btnVerOrdenes.addActionListener(e -> new OrdenesRegistradasVentana().setVisible(true));
		getContentPane().add(btnVerOrdenes);

		JButton btnSalir = new JButton("Salir");
		btnSalir.setBounds(670, 460, 100, 36);
		btnSalir.addActionListener(e -> salir());
		getContentPane().add(btnSalir);

		cargarCombos();
	}

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
}
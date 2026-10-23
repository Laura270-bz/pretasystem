package facitec.edu.py.pretasystem_abms;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_util.FormatoUtil;

/** Listado de ordenes de produccion ya guardadas, con el detalle de materia prima consumida. */
public class OrdenesRegistradasVentana extends JFrame {

	private static final long serialVersionUID = 1L;

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
		setTitle("Ordenes de Produccion registradas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 640);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JLabel lblBuscar = new JLabel("Buscar por etapa:");
		lblBuscar.setBounds(15, 15, 120, 22);
		getContentPane().add(lblBuscar);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(135, 15, 250, 24);
		getContentPane().add(txtBuscar);
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { cargarOrdenes(); }
			public void removeUpdate(DocumentEvent e) { cargarOrdenes(); }
			public void changedUpdate(DocumentEvent e) { cargarOrdenes(); }
		});

		JButton btnActualizar = new JButton("Actualizar");
		btnActualizar.setBounds(400, 14, 110, 26);
		btnActualizar.addActionListener(e -> cargarOrdenes());
		getContentPane().add(btnActualizar);

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
		tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaOrdenes.getColumnModel().getColumn(4).setCellRenderer(FormatoUtil.rendererMiles());
		tablaOrdenes.getColumnModel().getColumn(5).setCellRenderer(FormatoUtil.rendererMiles());
		tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				cargarDetalle();
			}
		});
		JScrollPane sp1 = new JScrollPane(tablaOrdenes);
		sp1.setBounds(15, 50, 955, 250);
		getContentPane().add(sp1);

		// ---------- DETALLE DE LA ORDEN SELECCIONADA ----------
		JLabel lblDetalle = new JLabel("Materia prima consumida en la orden seleccionada:");
		lblDetalle.setBounds(15, 315, 450, 22);
		getContentPane().add(lblDetalle);

		modeloDetalle = new DefaultTableModel(new Object[] { "Materia Prima", "Cantidad usada", "Unidad" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tablaDetalle = new JTable(modeloDetalle);
		tablaDetalle.getColumnModel().getColumn(1).setCellRenderer(FormatoUtil.rendererMiles());
		JScrollPane sp2 = new JScrollPane(tablaDetalle);
		sp2.setBounds(15, 345, 955, 245);
		getContentPane().add(sp2);

		cargarOrdenes();
	}

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
}
package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_util.FormatoUtil;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;

public class MateriaPrimaVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtNombre;
	private JSpinner spnStockActual;
	private JSpinner spnStockMinimo;
	private JComboBox<String> cmbUnidad;
	private JTextField txtBuscar;
	private JTable table;
	private DefaultTableModel modeloTabla;

	private final Materia_Prima_Dao dao = new Materia_Prima_Dao();
	private Materia_Prima materiaSeleccionada = null; // null = registro nuevo

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MateriaPrimaVentana frame = new MateriaPrimaVentana();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MateriaPrimaVentana() {
		setTitle("ABM- Materia Prima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1024, 680);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// ---------- PANEL FORMULARIO ----------
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 10, 278, 623);
		getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 10, 100, 14);
		panel.add(lblNombre);

		txtNombre = new JTextField();
		txtNombre.setBounds(10, 32, 262, 22);
		panel.add(txtNombre);

		JLabel lblStockActual = new JLabel("Stock Actual:");
		lblStockActual.setBounds(10, 65, 100, 14);
		panel.add(lblStockActual);

		spnStockActual = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
		FormatoUtil.aplicarMiles(spnStockActual);
		spnStockActual.setBounds(10, 85, 163, 24);
		panel.add(spnStockActual);

		cmbUnidad = new JComboBox<String>(new String[] { "Kg", "g", "L", "ml", "Unid." });
		cmbUnidad.setBounds(185, 85, 87, 24);
		panel.add(cmbUnidad);

		JLabel lblStockMinimo = new JLabel("Stock Minimo:");
		lblStockMinimo.setBounds(10, 122, 100, 14);
		panel.add(lblStockMinimo);

		spnStockMinimo = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
		FormatoUtil.aplicarMiles(spnStockMinimo);
		spnStockMinimo.setBounds(10, 144, 262, 27);
		panel.add(spnStockMinimo);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 480, 262, 6);
		panel.add(separator);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(10, 514, 96, 32);
		btnGuardar.addActionListener(e -> guardar());
		panel.add(btnGuardar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(154, 514, 96, 32);
		btnEliminar.addActionListener(e -> eliminar());
		panel.add(btnEliminar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(84, 581, 96, 32);
		btnCancelar.addActionListener(e -> limpiar());
		panel.add(btnCancelar);

		// ---------- BUSCADOR ----------
		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setBounds(311, 18, 50, 22);
		getContentPane().add(lblBuscar);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(365, 18, 300, 22);
		getContentPane().add(txtBuscar);
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { cargarTabla(); }
			public void removeUpdate(DocumentEvent e) { cargarTabla(); }
			public void changedUpdate(DocumentEvent e) { cargarTabla(); }
		});

		// ---------- TABLA (dentro de un JScrollPane para ver los encabezados) ----------
		modeloTabla = new DefaultTableModel(
				new Object[] { "ID", "Nombre", "Stock Actual", "Unidad", "Stock Minimo" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // la tabla es solo de lectura
			}
		};
		table = new JTable(modeloTabla);
		table.getColumnModel().getColumn(2).setCellRenderer(FormatoUtil.rendererMiles()); // Stock Actual
		table.getColumnModel().getColumn(4).setCellRenderer(FormatoUtil.rendererMiles()); // Stock Minimo
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				cargarFormularioDesdeTabla();
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBounds(311, 55, 676, 578);
		getContentPane().add(scroll);

		cargarTabla();
	}

	// ====================== LOGICA ======================

	private void cargarTabla() {
		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();
		List<Materia_Prima> lista = dao.recuperarPorFiltro(filtro);

		modeloTabla.setRowCount(0);
		for (Materia_Prima m : lista) {
			// AJUSTA los getters a los nombres reales de tu entidad
			modeloTabla.addRow(new Object[] { m.getId_materia_prima(), m.getNombre(), m.getStock_actual(),
					m.getUnidad_medida(), m.getStock_minimo() });
		}
	}

	private void guardar() {
		String nombre = txtNombre.getText().trim();
		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
			txtNombre.requestFocus();
			return;
		}

		try {
			Materia_Prima m = (materiaSeleccionada == null) ? new Materia_Prima() : materiaSeleccionada;

			// AJUSTA los setters a los nombres reales de tu entidad
			m.setNombre(nombre);
			m.setStock_actual(((Number) spnStockActual.getValue()).intValue());
			m.setUnidad_medida(cmbUnidad.getSelectedItem().toString());
			m.setStock_minimo(((Number) spnStockMinimo.getValue()).intValue());

			dao.guardar(m);
			JOptionPane.showMessageDialog(this, "Guardado correctamente");
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
		}
	}

	private void eliminar() {
		if (materiaSeleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla");
			return;
		}
		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + materiaSeleccionada.getNombre() + "\"?",
				"Confirmar", JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			dao.eliminar(materiaSeleccionada);
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
		Integer id = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();
		materiaSeleccionada = dao.recuperarPorId(id);
		if (materiaSeleccionada != null) {
			txtNombre.setText(materiaSeleccionada.getNombre());
			spnStockActual.setValue(materiaSeleccionada.getStock_actual());
			cmbUnidad.setSelectedItem(materiaSeleccionada.getUnidad_medida());
			spnStockMinimo.setValue(materiaSeleccionada.getStock_minimo());
		}
	}

	private void limpiar() {
		materiaSeleccionada = null;
		txtNombre.setText("");
		spnStockActual.setValue(0);
		spnStockMinimo.setValue(0);
		cmbUnidad.setSelectedIndex(0);
		table.clearSelection();
		txtNombre.requestFocus();
	}
}
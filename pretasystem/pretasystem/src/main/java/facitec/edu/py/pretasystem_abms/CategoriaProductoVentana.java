package facitec.edu.py.pretasystem_abms;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;

public class CategoriaProductoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtNombre;
	private JTable table;
	private DefaultTableModel modelo;
	private final Categoria_Producto_Dao dao = new Categoria_Producto_Dao();
	private Categoria_Producto seleccionada = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new CategoriaProductoVentana().setVisible(true));
	}

	public CategoriaProductoVentana() {
		setTitle("ABM - Categoria de Producto");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JLabel lbl = new JLabel("Nombre de la categoria:");
		lbl.setBounds(15, 15, 200, 16);
		getContentPane().add(lbl);

		txtNombre = new JTextField();
		txtNombre.setBounds(15, 38, 300, 26);
		getContentPane().add(txtNombre);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(330, 38, 100, 26);
		btnGuardar.addActionListener(e -> guardar());
		getContentPane().add(btnGuardar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(440, 38, 100, 26);
		btnEliminar.addActionListener(e -> eliminar());
		getContentPane().add(btnEliminar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(550, 38, 100, 26);
		btnCancelar.addActionListener(e -> limpiar());
		getContentPane().add(btnCancelar);

		modelo = new DefaultTableModel(new Object[] { "ID", "Categoria" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		table = new JTable(modelo);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
				int id = ((Number) modelo.getValueAt(table.getSelectedRow(), 0)).intValue();
				seleccionada = dao.recuperarPorId(id);
				if (seleccionada != null) {
					txtNombre.setText(seleccionada.getNombre_categoria());
				}
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(15, 85, 655, 310);
		getContentPane().add(sp);

		cargarTabla();
	}

	private void cargarTabla() {
		List<Categoria_Producto> lista = dao.recuperarPorFiltro("");
		modelo.setRowCount(0);
		for (Categoria_Producto c : lista) {
			modelo.addRow(new Object[] { c.getId_categoria_producto(), c.getNombre_categoria() });
		}
	}

	private void guardar() {
		String nombre = txtNombre.getText().trim();
		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
			return;
		}
		try {
			Categoria_Producto c = seleccionada == null ? new Categoria_Producto() : seleccionada;
			c.setNombre_categoria(nombre);
			dao.guardar(c);
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
		}
	}

	private void eliminar() {
		if (seleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una categoria de la tabla");
			return;
		}
		try {
			dao.eliminar(seleccionada);
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"No se pudo eliminar (puede estar en uso por algun producto).");
		}
	}

	private void limpiar() {
		seleccionada = null;
		txtNombre.setText("");
		table.clearSelection();
	}
}
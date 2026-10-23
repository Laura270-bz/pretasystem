package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Proveedor_Dao;
import facitec.edu.py.pretasystem_entidades.Proveedor;

public class ProveedorVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtDocumento;
	private JTextField txtNombre;
	private JTextField txtDireccion;
	private JTextField txtTelefono;
	private JTextField txtEmail;
	private JTextField txtBuscar;
	private JTable table;
	private DefaultTableModel modelo;
	private final Proveedor_Dao dao = new Proveedor_Dao();
	private Proveedor seleccionado = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ProveedorVentana().setVisible(true));
	}

	public ProveedorVentana() {
		setTitle("ABM - Proveedor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1100, 620);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel(null);
		panel.setBorder(new LineBorder(Color.BLACK));
		panel.setBounds(10, 10, 300, 563);
		getContentPane().add(panel);

		txtDocumento = texto(panel, "Documento (RUC/CI):", 10);
		txtNombre = texto(panel, "Nombre:", 70);
		txtDireccion = texto(panel, "Dirección:", 130);
		txtTelefono = texto(panel, "Teléfono:", 190);
		txtEmail = texto(panel, "Email:", 250);

		JSeparator sep = new JSeparator();
		sep.setBounds(10, 420, 280, 6);
		panel.add(sep);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(10, 440, 110, 32);
		btnGuardar.addActionListener(e -> guardar());
		panel.add(btnGuardar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(180, 440, 110, 32);
		btnEliminar.addActionListener(e -> eliminar());
		panel.add(btnEliminar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(95, 500, 110, 32);
		btnCancelar.addActionListener(e -> limpiar());
		panel.add(btnCancelar);

		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setBounds(330, 18, 50, 22);
		getContentPane().add(lblBuscar);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(385, 18, 300, 22);
		getContentPane().add(txtBuscar);
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { cargarTabla(); }
			public void removeUpdate(DocumentEvent e) { cargarTabla(); }
			public void changedUpdate(DocumentEvent e) { cargarTabla(); }
		});

		modelo = new DefaultTableModel(
				new Object[] { "ID", "Documento", "Nombre", "Dirección", "Teléfono", "Email" }, 0) {
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
				seleccionado = dao.recuperarPorId(id);
				if (seleccionado != null) {
					txtDocumento.setText(seleccionado.getDocumento());
					txtNombre.setText(seleccionado.getNombre());
					txtDireccion.setText(seleccionado.getDireccion());
					txtTelefono.setText(seleccionado.getTelefono());
					txtEmail.setText(seleccionado.getEmail());
				}
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(330, 55, 745, 518);
		getContentPane().add(sp);

		cargarTabla();
	}

	private JTextField texto(JPanel p, String etiqueta, int y) {
		JLabel l = new JLabel(etiqueta);
		l.setBounds(10, y, 280, 14);
		p.add(l);
		JTextField t = new JTextField();
		t.setBounds(10, y + 20, 280, 26);
		p.add(t);
		return t;
	}

	private void cargarTabla() {
		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();
		List<Proveedor> lista = dao.recuperarPorFiltro(filtro);
		modelo.setRowCount(0);
		for (Proveedor p : lista) {
			modelo.addRow(new Object[] { p.getId_proveedores(), p.getDocumento(), p.getNombre(), p.getDireccion(),
					p.getTelefono(), p.getEmail() });
		}
	}

	private void guardar() {
		String doc = txtDocumento.getText().trim();
		String nombre = txtNombre.getText().trim();
		String dir = txtDireccion.getText().trim();
		String tel = txtTelefono.getText().trim();
		String email = txtEmail.getText().trim();

		if (doc.isEmpty() || nombre.isEmpty() || dir.isEmpty() || tel.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
			return;
		}
		if (!email.contains("@")) {
			JOptionPane.showMessageDialog(this, "El email no es válido");
			return;
		}
		try {
			Proveedor p = seleccionado == null ? new Proveedor() : seleccionado;
			p.setDocumento(doc);
			p.setNombre(nombre);
			p.setDireccion(dir);
			p.setTelefono(tel);
			p.setEmail(email);
			dao.guardar(p);
			JOptionPane.showMessageDialog(this, "Guardado correctamente");
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo guardar. ¿Ya existe un proveedor con ese documento?");
		}
	}

	private void eliminar() {
		if (seleccionado == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla");
			return;
		}
		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionado.getNombre() + "\"?", "Confirmar",
				JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			dao.eliminar(seleccionado);
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede estar en uso).");
		}
	}

	private void limpiar() {
		seleccionado = null;
		txtDocumento.setText("");
		txtNombre.setText("");
		txtDireccion.setText("");
		txtTelefono.setText("");
		txtEmail.setText("");
		table.clearSelection();
	}
}
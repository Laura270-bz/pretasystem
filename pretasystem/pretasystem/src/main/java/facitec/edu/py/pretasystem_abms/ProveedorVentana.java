
package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
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

	// Guarda el proveedor seleccionado.
	private Proveedor seleccionado = null;

	// Indica si el formulario está en modo edición.
	private boolean modoEdicion = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ProveedorVentana().setVisible(true));
	}

	public ProveedorVentana() {

		setTitle("ABM - Proveedor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1100, 620);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// ---------- PANEL FORMULARIO ----------

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

		// ---------- BOTÓN GUARDAR ----------

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(10, 440, 110, 32);
		btnGuardar.addActionListener(e -> guardar());
		panel.add(btnGuardar);

		// ---------- BOTÓN EDITAR ----------

		JButton btnEditar = new JButton("Editar");
		btnEditar.setBounds(180, 440, 110, 32);
		btnEditar.addActionListener(e -> editar());
		panel.add(btnEditar);

		// ---------- BOTÓN ELIMINAR ----------

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(10, 480, 110, 32);
		btnEliminar.addActionListener(e -> eliminar());
		panel.add(btnEliminar);

		// ---------- BOTÓN SALIR ----------

		JButton btnSalir = new JButton("Salir");
		btnSalir.setBounds(180, 480, 110, 32);
		btnSalir.addActionListener(e -> salir());
		panel.add(btnSalir);

		// ---------- BOTÓN CANCELAR ----------

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(95, 520, 110, 32);
		btnCancelar.addActionListener(e -> limpiar());
		panel.add(btnCancelar);

		// ---------- BUSCADOR ----------

		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setBounds(330, 18, 50, 22);
		getContentPane().add(lblBuscar);

		// Icono de lupa.
		JLabel lblLupa = new JLabel("\uD83D\uDD0D");
		lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
		lblLupa.setBounds(375, 15, 25, 25);
		getContentPane().add(lblLupa);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(405, 18, 300, 22);
		getContentPane().add(txtBuscar);

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

		// ---------- TABLA ----------

		modelo = new DefaultTableModel(new Object[] { "ID", "Documento", "Nombre", "Dirección", "Teléfono", "Email" },
				0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		table = new JTable(modelo);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Al seleccionar una fila, recupera el proveedor,
		// pero no carga todavía sus datos en el formulario.
		table.getSelectionModel().addListSelectionListener(e -> {

			if (!e.getValueIsAdjusting()) {
				seleccionarRegistro();
			}
		});

		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(330, 55, 745, 518);
		getContentPane().add(sp);

		cargarTabla();
	}

	/**
	 * Crea un campo de texto con su etiqueta.
	 */
	private JTextField texto(JPanel p, String etiqueta, int y) {

		JLabel l = new JLabel(etiqueta);
		l.setBounds(10, y, 280, 14);
		p.add(l);

		JTextField t = new JTextField();
		t.setBounds(10, y + 20, 280, 26);
		p.add(t);

		return t;
	}

	/**
	 * Recupera los proveedores de la base de datos y los muestra en la tabla.
	 */
	private void cargarTabla() {

		String filtro = txtBuscar == null ? "" : txtBuscar.getText().trim();

		List<Proveedor> lista = dao.recuperarPorFiltro(filtro);

		modelo.setRowCount(0);

		for (Proveedor p : lista) {

			modelo.addRow(new Object[] { p.getId_proveedores(), p.getDocumento(), p.getNombre(), p.getDireccion(),
					p.getTelefono(), p.getEmail() });
		}
	}

	/**
	 * Recupera el proveedor seleccionado en la tabla. No carga sus datos en el
	 * formulario.
	 */
	private void seleccionarRegistro() {

		int fila = table.getSelectedRow();

		if (fila < 0) {
			seleccionado = null;
			modoEdicion = false;
			return;
		}

		// Obtiene el ID del proveedor seleccionado.
		int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

		// Recupera el proveedor desde la base de datos.
		seleccionado = dao.recuperarPorId(id);

		// Todavía no se activa el modo edición.
		modoEdicion = false;
	}

	/**
	 * Registra un proveedor nuevo o actualiza el proveedor seleccionado cuando está
	 * en edición.
	 */
	private void guardar() {

		String doc = txtDocumento.getText().trim();
		String nombre = txtNombre.getText().trim();
		String dir = txtDireccion.getText().trim();
		String tel = txtTelefono.getText().trim();
		String email = txtEmail.getText().trim();

		// Validación de campos obligatorios.
		if (doc.isEmpty() || nombre.isEmpty() || dir.isEmpty() || tel.isEmpty() || email.isEmpty()) {

			JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");

			return;
		}

		// Validación básica del correo electrónico.
		if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {

			JOptionPane.showMessageDialog(this, "El email no es válido");

			return;
		}

		try {

			if (modoEdicion) {

				// Comprueba que exista un proveedor seleccionado.
				if (seleccionado == null) {

					JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar");

					return;
				}

				// Actualiza el proveedor existente.
				seleccionado.setDocumento(doc);
				seleccionado.setNombre(nombre);
				seleccionado.setDireccion(dir);
				seleccionado.setTelefono(tel);
				seleccionado.setEmail(email);

				// Guarda los cambios en la base de datos.
				dao.guardar(seleccionado);

				JOptionPane.showMessageDialog(this, "Proveedor editado correctamente");

			} else {

				// Crea un proveedor nuevo.
				Proveedor p = new Proveedor();

				p.setDocumento(doc);
				p.setNombre(nombre);
				p.setDireccion(dir);
				p.setTelefono(tel);
				p.setEmail(email);

				// Guarda el nuevo proveedor.
				dao.guardar(p);

				JOptionPane.showMessageDialog(this, "Proveedor guardado correctamente");
			}

			// Limpia el formulario y actualiza la tabla.
			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this,
					"No se pudo guardar. Verifique si el documento ya existe.\n" + ex.getMessage());
		}
	}

	/**
	 * Carga los datos del proveedor seleccionado en el formulario y activa el modo
	 * edición.
	 */
	private void editar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla");

			return;
		}

		// Carga los datos del proveedor en los campos.
		txtDocumento.setText(seleccionado.getDocumento());
		txtNombre.setText(seleccionado.getNombre());
		txtDireccion.setText(seleccionado.getDireccion());
		txtTelefono.setText(seleccionado.getTelefono());
		txtEmail.setText(seleccionado.getEmail());

		// Activa el modo edición.
		modoEdicion = true;

		JOptionPane.showMessageDialog(this, "Puede modificar los datos del proveedor seleccionado");
	}

	/**
	 * Elimina el proveedor seleccionado.
	 */
	private void eliminar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla");

			return;
		}

		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionado.getNombre() + "\"?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			dao.eliminar(seleccionado);

			JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente");

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this,
					"No se pudo eliminar. El proveedor puede estar en uso.\n" + ex.getMessage());
		}
	}

	/**
	 * Limpia los campos y desactiva el modo edición.
	 */
	private void limpiar() {

		seleccionado = null;
		modoEdicion = false;

		txtDocumento.setText("");
		txtNombre.setText("");
		txtDireccion.setText("");
		txtTelefono.setText("");
		txtEmail.setText("");

		table.clearSelection();

		txtDocumento.requestFocus();
	}

	/**
	 * Cierra la ventana de proveedores.
	 */
	private void salir() {

		dispose();
	}
}

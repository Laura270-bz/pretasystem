
package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.SQLException;
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

		txtBuscar = new JTextField();
		txtBuscar.setBounds(385, 18, 300, 25);
		getContentPane().add(txtBuscar);

		// Icono de lupa a la derecha del campo de búsqueda.
		JLabel lblLupa = new JLabel("\uD83D\uDD0D");
		lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
		lblLupa.setBounds(690, 16, 30, 28);
		getContentPane().add(lblLupa);

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

		// Seleccionar una fila recupera el proveedor,
		// pero no carga sus datos hasta pulsar Editar.
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
	 * Recupera los proveedores y los muestra en la tabla.
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
	 * Recupera el proveedor seleccionado en la tabla.
	 */
	private void seleccionarRegistro() {

		int fila = table.getSelectedRow();

		if (fila < 0) {
			seleccionado = null;
			modoEdicion = false;
			return;
		}

		int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

		seleccionado = dao.recuperarPorId(id);

		modoEdicion = false;
	}

	/**
	 * Registra un proveedor nuevo o actualiza uno existente.
	 */
	private void guardar() {

		String doc = txtDocumento.getText().trim();
		String nombre = txtNombre.getText().trim();
		String dir = txtDireccion.getText().trim();
		String tel = txtTelefono.getText().trim();
		String email = txtEmail.getText().trim();

		// Validación de campos obligatorios.
		if (doc.isEmpty() || nombre.isEmpty() || dir.isEmpty() || tel.isEmpty() || email.isEmpty()) {

			JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Validación básica del correo electrónico.
		if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {

			JOptionPane.showMessageDialog(this, "El email no es válido", "Validación", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {

			// En edición se excluye el ID del proveedor actual.
			Integer idExcluir = null;

			if (modoEdicion) {

				if (seleccionado == null) {

					JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar", "Atención",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				idExcluir = seleccionado.getId_proveedores();
			}

			// Comprueba si el CI/RUC ya pertenece a otro proveedor.
			if (dao.existeDocumento(doc, idExcluir)) {

				JOptionPane.showMessageDialog(this,
						"Ya existe un proveedor registrado con ese CI/RUC.\n",
						"Documento duplicado", JOptionPane.WARNING_MESSAGE);

				txtDocumento.requestFocus();
				txtDocumento.selectAll();

				return;
			}

			if (modoEdicion) {

				// Actualiza el proveedor seleccionado.
				seleccionado.setDocumento(doc);
				seleccionado.setNombre(nombre);
				seleccionado.setDireccion(dir);
				seleccionado.setTelefono(tel);
				seleccionado.setEmail(email);

				dao.guardar(seleccionado);

				JOptionPane.showMessageDialog(this, "Proveedor editado correctamente", "Operación exitosa",
						JOptionPane.INFORMATION_MESSAGE);

			} else {

				// Registra un proveedor nuevo.
				Proveedor p = new Proveedor();

				p.setDocumento(doc);
				p.setNombre(nombre);
				p.setDireccion(dir);
				p.setTelefono(tel);
				p.setEmail(email);

				dao.guardar(p);

				JOptionPane.showMessageDialog(this, "Proveedor guardado correctamente", "Operación exitosa",
						JOptionPane.INFORMATION_MESSAGE);
			}

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			// Evita mostrar al usuario el error técnico de Hibernate o SQL.
			if (esErrorDeDocumentoDuplicado(ex)) {

				JOptionPane.showMessageDialog(this,
						"Ya existe un proveedor registrado con ese CI/RUC.\n" + "Ingrese un documento diferente.",
						"Documento duplicado", JOptionPane.WARNING_MESSAGE);

				txtDocumento.requestFocus();
				txtDocumento.selectAll();

			} else {

				JOptionPane.showMessageDialog(this,
						"No se pudo guardar el proveedor. Verifique los datos e intente nuevamente.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Identifica errores de clave única o integridad de la base de datos.
	 */
	private boolean esErrorDeDocumentoDuplicado(Throwable ex) {

		Throwable causa = ex;

		while (causa != null) {

			if (causa instanceof SQLException) {

				String estado = ((SQLException) causa).getSQLState();

				// PostgreSQL: 23505.
				// MySQL: 23000.
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

	/**
	 * Carga los datos del proveedor seleccionado y activa la edición.
	 */
	private void editar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla", "Atención",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		txtDocumento.setText(seleccionado.getDocumento());
		txtNombre.setText(seleccionado.getNombre());
		txtDireccion.setText(seleccionado.getDireccion());
		txtTelefono.setText(seleccionado.getTelefono());
		txtEmail.setText(seleccionado.getEmail());

		modoEdicion = true;

		JOptionPane.showMessageDialog(this, "Puede modificar los datos del proveedor seleccionado", "Modo edición",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Elimina el proveedor seleccionado.
	 */
	private void eliminar() {

		if (seleccionado == null) {

			JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla", "Atención",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionado.getNombre() + "\"?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			dao.eliminar(seleccionado);

			JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente", "Operación exitosa",
					JOptionPane.INFORMATION_MESSAGE);

			limpiar();
			cargarTabla();

		} catch (Exception ex) {

			JOptionPane.showMessageDialog(this,
					"No se pudo eliminar el proveedor. Es posible que esté relacionado con otros registros.",
					"Error al eliminar", JOptionPane.ERROR_MESSAGE);
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
	 * Cierra la ventana.
	 */
	private void salir() {

		dispose();
	}
}

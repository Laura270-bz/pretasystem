package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;

public class CategoriaProductoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtNombre;
	private JTextField txtBuscar;
	private JButton btnGuardar;
	private JButton btnEditar;
	private JButton btnEliminar;
	private JTable table;
	private DefaultTableModel modelo;
	private final Categoria_Producto_Dao dao = new Categoria_Producto_Dao();
	private Categoria_Producto seleccionada = null;
	private boolean cargando = false; // evita que recargar la tabla dispare el listener de seleccion

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new CategoriaProductoVentana().setVisible(true));
	}

	public CategoriaProductoVentana() {
		setTitle("ABM - Categoria de Producto");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// ---------- Fila 1: nombre + Guardar / Cancelar ----------
		JLabel lbl = new JLabel("Nombre de la categoria:");
		lbl.setBounds(15, 15, 200, 16);
		getContentPane().add(lbl);

		txtNombre = new JTextField();
		txtNombre.setBounds(15, 38, 300, 26);
		txtNombre.addActionListener(e -> guardar()); // Enter guarda
		getContentPane().add(txtNombre);

		btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(330, 38, 100, 26);
		btnGuardar.addActionListener(e -> guardar());
		getContentPane().add(btnGuardar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(440, 38, 100, 26);
		btnCancelar.addActionListener(e -> modoNuevo());
		getContentPane().add(btnCancelar);

		// ---------- Fila 2: buscador con lupa + Editar / Eliminar / Salir ----------
		JLabel lblLupa = new JLabel(new LupaIcon());
		lblLupa.setBounds(15, 80, 24, 28);
		getContentPane().add(lblLupa);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(42, 80, 273, 28);
		txtBuscar.setToolTipText("Buscar categoria por nombre");
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
		getContentPane().add(txtBuscar);

		btnEditar = new JButton("Editar");
		btnEditar.setBounds(330, 80, 100, 28);
		btnEditar.addActionListener(e -> editar());
		getContentPane().add(btnEditar);

		btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(440, 80, 100, 28);
		btnEliminar.addActionListener(e -> eliminar());
		getContentPane().add(btnEliminar);

		JButton btnSalir = new JButton("Salir");
		btnSalir.setBounds(570, 80, 100, 28);
		btnSalir.addActionListener(e -> dispose());
		getContentPane().add(btnSalir);

		// ---------- Tabla ----------
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
			if (cargando || e.getValueIsAdjusting() || table.getSelectedRow() < 0) {
				return;
			}
			int id = ((Number) modelo.getValueAt(table.getSelectedRow(), 0)).intValue();
			seleccionada = dao.recuperarPorId(id);
			if (seleccionada != null) {
				txtNombre.setText(seleccionada.getNombre_categoria());
				modoSeleccion();
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(15, 125, 655, 330);
		getContentPane().add(sp);

		cargarTabla();
		modoNuevo();
	}

	// ====================== MODOS DE LA PANTALLA ======================

	/** Formulario vacio, listo para crear una categoria nueva. */
	private void modoNuevo() {
		seleccionada = null;
		txtNombre.setText("");
		table.clearSelection();
		txtNombre.setEnabled(true);
		btnGuardar.setEnabled(true);
		btnEditar.setEnabled(false);
		btnEliminar.setEnabled(false);
	}

	/** Hay una categoria elegida en la tabla: se ve, pero no se modifica hasta tocar Editar. */
	private void modoSeleccion() {
		txtNombre.setEnabled(false);
		btnGuardar.setEnabled(false);
		btnEditar.setEnabled(true);
		btnEliminar.setEnabled(true);
	}

	/** Se habilita el nombre para modificarlo y guardar. */
	private void modoEdicion() {
		txtNombre.setEnabled(true);
		btnGuardar.setEnabled(true);
		btnEditar.setEnabled(false);
		txtNombre.requestFocusInWindow();
		txtNombre.selectAll();
	}

	// ====================== ACCIONES ======================

	// Carga la tabla aplicando el texto del buscador (busca dentro del nombre, sin distinguir mayusculas)
	private void cargarTabla() {
		String filtro = txtBuscar.getText().trim().toLowerCase();
		List<Categoria_Producto> lista = dao.recuperarPorFiltro("");
		cargando = true;
		try {
			modelo.setRowCount(0);
			int filaSeleccionada = -1;
			for (Categoria_Producto c : lista) {
				String nombre = c.getNombre_categoria() == null ? "" : c.getNombre_categoria();
				if (filtro.isEmpty() || nombre.toLowerCase().contains(filtro)) {
					modelo.addRow(new Object[] { c.getId_categoria_producto(), nombre });
					if (seleccionada != null && c.getId_categoria_producto() == seleccionada.getId_categoria_producto()) {
						filaSeleccionada = modelo.getRowCount() - 1;
					}
				}
			}
			// si la categoria elegida sigue visible, se mantiene marcada
			if (filaSeleccionada >= 0) {
				table.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
			}
		} finally {
			cargando = false;
		}
	}

	private void editar() {
		if (seleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una categoria de la tabla");
			return;
		}
		modoEdicion();
	}

	private void guardar() {
		if (!btnGuardar.isEnabled()) {
			return; // Enter en el campo cuando no se puede guardar
		}
		String nombre = txtNombre.getText().trim();
		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
			return;
		}
		try {
			Categoria_Producto c = seleccionada == null ? new Categoria_Producto() : seleccionada;
			c.setNombre_categoria(nombre);
			dao.guardar(c);
			modoNuevo();
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
		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionada.getNombre_categoria() + "\"?",
				"Confirmar", JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			dao.eliminar(seleccionada);
			modoNuevo();
			cargarTabla();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede estar en uso por algun producto).");
		}
	}

	// ====================== ICONO DE LUPA ======================

	/** Lupa dibujada con codigo (no necesita archivos de imagen). */
	private static class LupaIcon implements Icon {
		@Override
		public int getIconWidth() {
			return 20;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(236, 64, 122));
			g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawOval(x + 2, y + 2, 11, 11);
			g2.drawLine(x + 12, y + 12, x + 18, y + 18);
			g2.dispose();
		}
	}
}
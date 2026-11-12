package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import facitec.edu.py.pretasystem_dao.Usuario_Dao;
import facitec.edu.py.pretasystem_entidades.Usuario;
import facitec.edu.py.pretasystem_util.SeguridadUtil;
import facitec.edu.py.pretasystem_util.SesionUsuario;

public class UsuarioVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField txtUsuario;
	private JTextField txtCorreo;
	private JPasswordField txtClave;
	private JPasswordField txtClave2;
	private JTable table;
	private DefaultTableModel modelo;
	private final Usuario_Dao dao = new Usuario_Dao();
	private Usuario seleccionado = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new UsuarioVentana().setVisible(true));
	}

	public UsuarioVentana() {
		setTitle("ABM - Usuario");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 560);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel(null);
		panel.setBorder(new LineBorder(Color.BLACK));
		panel.setBounds(10, 10, 300, 503);
		getContentPane().add(panel);

		txtUsuario = texto(panel, "Usuario:", 10);
		txtCorreo = texto(panel, "Correo:", 70);
		txtClave = clave(panel, "Contraseña:", 130);
		txtClave2 = clave(panel, "Repetir contraseña:", 190);
		activarLimpiezaDePuntos();

		JLabel nota = new JLabel(
				"<html>Al editar, dejá los asteriscos para<br>mantener la contraseña actual<br>o escribí una nueva.</html>");
		nota.setBounds(10, 250, 280, 50);
		panel.add(nota);

		// Solo el administrador puede registrar, editar y eliminar usuarios
		boolean esAdmin = puedeAdministrar();
		// el título indica con qué rol estás usando la ventana
		setTitle("ABM - Usuario" + (SesionUsuario.esAdministrador() ? "  (Administrador)" : ""));
		txtUsuario.setEnabled(esAdmin);
		txtCorreo.setEnabled(esAdmin);
		txtClave.setEnabled(esAdmin);
		txtClave2.setEnabled(esAdmin);
		if (!esAdmin) {
			nota.setText("<html>Solo el administrador puede<br>registrar, editar o eliminar<br>usuarios.</html>");
		}

		JSeparator sep = new JSeparator();
		sep.setBounds(10, 380, 280, 6);
		panel.add(sep);

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(10, 400, 110, 32);
		btnGuardar.addActionListener(e -> guardar());
		panel.add(btnGuardar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(180, 400, 110, 32);
		btnEliminar.addActionListener(e -> eliminar());
		panel.add(btnEliminar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(95, 450, 110, 32);
		btnCancelar.addActionListener(e -> limpiar());
		panel.add(btnCancelar);

		btnGuardar.setEnabled(esAdmin);
		btnEliminar.setEnabled(esAdmin);

		modelo = new DefaultTableModel(new Object[] { "ID", "Usuario", "Correo" }, 0) {
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
					txtUsuario.setText(seleccionado.getUsuario());
					txtCorreo.setText(seleccionado.getCorreo());
					// la contraseña se guarda cifrada (no se puede mostrar); se muestran asteriscos
					txtClave.setText(PUNTOS);
					txtClave2.setText(PUNTOS);
				}
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(330, 10, 545, 503);
		getContentPane().add(sp);

		cargarTabla();
	}

	private static final String PUNTOS = "********";

	// Al hacer clic en una contraseña que muestra los asteriscos, se vacían ambos campos para escribir una nueva
	private void activarLimpiezaDePuntos() {
		FocusAdapter fa = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (new String(txtClave.getPassword()).equals(PUNTOS)) {
					txtClave.setText("");
					txtClave2.setText("");
				}
			}
		};
		txtClave.addFocusListener(fa);
		txtClave2.addFocusListener(fa);
	}

	// Es administrador, o todavía no hay ningún usuario registrado (primer usuario)
	private boolean puedeAdministrar() {
		return SesionUsuario.esAdministrador() || dao.contarUsuarios() == 0;
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

	private JPasswordField clave(JPanel p, String etiqueta, int y) {
		JLabel l = new JLabel(etiqueta);
		l.setBounds(10, y, 280, 14);
		p.add(l);
		JPasswordField t = new JPasswordField();
		t.setBounds(10, y + 20, 280, 26);
		p.add(t);
		return t;
	}

	private void cargarTabla() {
		List<Usuario> lista = dao.recuperarPorFiltro("");
		modelo.setRowCount(0);
		for (Usuario u : lista) {
			modelo.addRow(new Object[] { u.getId_usuario(), u.getUsuario(), u.getCorreo() });
		}
	}

	private void guardar() {
		String nombre = txtUsuario.getText().trim();
		String correo = txtCorreo.getText().trim();
		String c1 = new String(txtClave.getPassword());
		String c2 = new String(txtClave2.getPassword());
		// si quedaron los asteriscos, la contraseña no se modifica
		if (c1.equals(PUNTOS)) {
			c1 = "";
		}
		if (c2.equals(PUNTOS)) {
			c2 = "";
		}

		if (!puedeAdministrar()) {
			JOptionPane.showMessageDialog(this, "Solo el administrador puede registrar o editar usuarios");
			return;
		}
		if (nombre.isEmpty() || correo.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Usuario y correo son obligatorios");
			return;
		}
		if (!correo.contains("@")) {
			JOptionPane.showMessageDialog(this, "El correo no es válido");
			return;
		}
		if (seleccionado == null && c1.isEmpty()) {
			JOptionPane.showMessageDialog(this, "La contraseña es obligatoria");
			return;
		}
		if (!c1.isEmpty()) {
			if (c1.length() < 4) {
				JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres");
				return;
			}
			if (!c1.equals(c2)) {
				JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
				return;
			}
		}
		try {
			Usuario u = seleccionado == null ? new Usuario() : seleccionado;
			u.setUsuario(nombre);
			u.setCorreo(correo);
			if (!c1.isEmpty()) {
				u.setContrasena(SeguridadUtil.sha256(c1));
			}
			dao.guardar(u);
			JOptionPane.showMessageDialog(this, "Guardado correctamente");
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo guardar. ¿Ya existe ese nombre de usuario?");
		}
	}

	private void eliminar() {
		if (!puedeAdministrar()) {
			JOptionPane.showMessageDialog(this, "Solo el administrador puede eliminar usuarios");
			return;
		}
		if (seleccionado == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla");
			return;
		}
		Usuario actual = SesionUsuario.getUsuario();
		if (actual != null && actual.getId_usuario() == seleccionado.getId_usuario()) {
			JOptionPane.showMessageDialog(this, "No podés eliminar el usuario con el que iniciaste sesión");
			return;
		}
		int op = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + seleccionado.getUsuario() + "\"?", "Confirmar",
				JOptionPane.YES_NO_OPTION);
		if (op != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			dao.eliminar(seleccionado);
			limpiar();
			cargarTabla();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo eliminar (puede tener órdenes de producción asociadas).");
		}
	}

	private void limpiar() {
		seleccionado = null;
		txtUsuario.setText("");
		txtCorreo.setText("");
		txtClave.setText("");
		txtClave2.setText("");
		table.clearSelection();
	}
}
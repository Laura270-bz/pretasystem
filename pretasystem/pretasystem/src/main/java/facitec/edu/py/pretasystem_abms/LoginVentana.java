package facitec.edu.py.pretasystem_abms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import facitec.edu.py.pretasystem_dao.Usuario_Dao;
import facitec.edu.py.pretasystem_entidades.Usuario;
import facitec.edu.py.pretasystem_util.SeguridadUtil;
import facitec.edu.py.pretasystem_util.SesionUsuario;

/** Pantalla de inicio de sesion (RF-1) con diseño rosado. */
public class LoginVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);
	private static final Color FONDO = new Color(255, 245, 248);

	private JTextField txtUsuario;
	private JPasswordField txtClave;
	private JLabel lblMensaje;
	private final Usuario_Dao dao = new Usuario_Dao();

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new LoginVentana().setVisible(true));
	}

	public LoginVentana() {
		setTitle("PretaSystem - Iniciar sesión");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(780, 450);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// ---------- LADO IZQUIERDO (degradado rosa con circulos decorativos) ----------
		JPanel izquierdo = new JPanel(null) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setPaint(new GradientPaint(0, 0, ROSA_MEDIO, 0, getHeight(), ROSA_FUERTE));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(new Color(255, 255, 255, 55));
				g2.fillOval(-60, -60, 200, 200);
				g2.fillOval(180, 300, 220, 220);
				g2.setColor(new Color(255, 255, 255, 35));
				g2.fillOval(150, 40, 110, 110);
				g2.fillOval(-40, 280, 120, 120);
				g2.dispose();
			}
		};
		izquierdo.setBounds(0, 0, 320, 450);
		getContentPane().add(izquierdo);

		JLabel lblLogo = new JLabel("PretaSystem", SwingConstants.CENTER);
		lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblLogo.setForeground(Color.WHITE);
		lblLogo.setBounds(10, 170, 300, 44);
		izquierdo.add(lblLogo);

		JLabel lblSub = new JLabel("Fábrica de trapillos", SwingConstants.CENTER);
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSub.setForeground(Color.WHITE);
		lblSub.setBounds(10, 215, 300, 24);
		izquierdo.add(lblSub);

		// ---------- LADO DERECHO (formulario) ----------
		JPanel derecho = new JPanel(null);
		derecho.setBackground(FONDO);
		derecho.setBounds(320, 0, 460, 450);
		getContentPane().add(derecho);

		JLabel lblTitulo = new JLabel("Bienvenido/a");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitulo.setForeground(ROSA_OSCURO);
		lblTitulo.setBounds(60, 50, 340, 40);
		derecho.add(lblTitulo);

		JLabel lblIndicacion = new JLabel("Ingresá tus datos para continuar");
		lblIndicacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblIndicacion.setForeground(new Color(150, 100, 120));
		lblIndicacion.setBounds(60, 92, 340, 20);
		derecho.add(lblIndicacion);

		derecho.add(etiqueta("Usuario", 135));
		txtUsuario = new JTextField();
		estilizar(txtUsuario);
		txtUsuario.setBounds(60, 158, 340, 36);
		derecho.add(txtUsuario);

		derecho.add(etiqueta("Contraseña", 205));
		txtClave = new JPasswordField();
		estilizar(txtClave);
		txtClave.setBounds(60, 228, 340, 36);
		derecho.add(txtClave);

		JCheckBox chkMostrar = new JCheckBox("Mostrar contraseña");
		chkMostrar.setOpaque(false);
		chkMostrar.setForeground(ROSA_OSCURO);
		chkMostrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		chkMostrar.setBounds(56, 270, 200, 22);
		char eco = txtClave.getEchoChar();
		chkMostrar.addActionListener(e -> txtClave.setEchoChar(chkMostrar.isSelected() ? (char) 0 : eco));
		derecho.add(chkMostrar);

		lblMensaje = new JLabel(" ");
		lblMensaje.setForeground(new Color(200, 30, 60));
		lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblMensaje.setBounds(60, 298, 340, 20);
		derecho.add(lblMensaje);

		BotonRedondo btnIngresar = new BotonRedondo("Ingresar");
		btnIngresar.setBounds(60, 330, 340, 42);
		btnIngresar.addActionListener(e -> ingresar());
		derecho.add(btnIngresar);
		getRootPane().setDefaultButton(btnIngresar); // Enter = ingresar

		// Primer uso: si no hay usuarios, hay que crear el primero
		try {
			if (dao.contarUsuarios() == 0) {
				JOptionPane.showMessageDialog(this, "No hay usuarios registrados.\nCree el primer usuario para poder ingresar.");
				new UsuarioVentana().setVisible(true);
			}
		} catch (Exception ex) {
			lblMensaje.setText("No se pudo conectar con la base de datos");
			ex.printStackTrace();
		}
	}

	private JLabel etiqueta(String texto, int y) {
		JLabel l = new JLabel(texto);
		l.setFont(new Font("Segoe UI", Font.BOLD, 13));
		l.setForeground(ROSA_OSCURO);
		l.setBounds(60, y, 340, 20);
		return l;
	}

	private void estilizar(JTextField campo) {
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		campo.setBackground(Color.WHITE);
		campo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ROSA_MEDIO, 2, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
	}

	private void ingresar() {
		String nombre = txtUsuario.getText().trim();
		String clave = new String(txtClave.getPassword());
		if (nombre.isEmpty() || clave.isEmpty()) {
			lblMensaje.setText("Completá usuario y contraseña");
			return;
		}
		try {
			Usuario u = dao.autenticar(nombre, SeguridadUtil.sha256(clave));
			if (u == null) {
				lblMensaje.setText("Usuario o contraseña incorrectos");
				txtClave.setText("");
				return;
			}
			SesionUsuario.setUsuario(u);
			dispose();

			new PantallaPrincipal().setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			lblMensaje.setText("Error de conexión con la base de datos");
		}
	}

	/** Boton rosado con bordes redondeados. */
	private static class BotonRedondo extends JButton {
		private static final long serialVersionUID = 1L;

		BotonRedondo(String texto) {
			super(texto);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorderPainted(false);
			setForeground(Color.WHITE);
			setFont(new Font("Segoe UI", Font.BOLD, 15));
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
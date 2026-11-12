
package facitec.edu.py.pretasystem_abms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import facitec.edu.py.pretasystem_entidades.Usuario;
import facitec.edu.py.pretasystem_reportes.ReporteMateriaPrimaVentana;
import facitec.edu.py.pretasystem_reportes.ReporteProductoTerminadoVentana;
import facitec.edu.py.pretasystem_reportes.ReporteProveedorVentana;
import facitec.edu.py.pretasystem_reportes.ReporteOrdenProduccionVentana;
import facitec.edu.py.pretasystem_reportes.ReporteConsumoMateriaPrimaVentana;
import facitec.edu.py.pretasystem_reportes.ReporteCategoriaProductoVentana;
import facitec.edu.py.pretasystem_util.ConecctionHelper;
import facitec.edu.py.pretasystem_util.Iconos;
import facitec.edu.py.pretasystem_util.SesionUsuario;

/**
 * Ventana principal del sistema PretaSystem.
 */
public class PantallaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color ROSA_FUERTE = new Color(236, 64, 122);
	private static final Color ROSA_OSCURO = new Color(173, 20, 87);
	private static final Color ROSA_MEDIO = new Color(244, 143, 177);
	private static final Color ROSA_CLARO = new Color(252, 228, 236);

	private final Map<String, JFrame> abiertas = new HashMap<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new PantallaPrincipal().setVisible(true));
	}

	public PantallaPrincipal() {

		setTitle("PretaSystem - Fábrica de Trapillos");
		setIconImage(Iconos.imagen("ovillo", 64, ROSA_FUERTE));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salirAplicacion();
			}
		});

		setBounds(100, 100, 1024, 680);
		setLocationRelativeTo(null);

		setContentPane(new FondoPanel());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(crearEncabezado(), BorderLayout.NORTH);
		getContentPane().add(crearTarjetas(), BorderLayout.CENTER);
		setJMenuBar(crearMenu());
	}

	// ====================== ENCABEZADO ======================

	private JPanel crearEncabezado() {

		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(25, 40, 10, 40));

		Usuario u = SesionUsuario.getUsuario();
		String nombre = (u == null) ? "Invitado/a" : u.getUsuario();

		JPanel textos = new JPanel(new GridLayout(2, 1));
		textos.setOpaque(false);

		JLabel titulo = new JLabel("PretaSystem");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
		titulo.setForeground(ROSA_OSCURO);

		JLabel sub = new JLabel("Bienvenido/a, " + nombre + (SesionUsuario.esAdministrador() ? " (Administrador)" : "")
				+ "  ·  Fábrica de trapillos");

		sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		sub.setForeground(new Color(150, 80, 110));

		textos.add(titulo);
		textos.add(sub);
		p.add(textos, BorderLayout.WEST);

		BotonRosa btnSalir = new BotonRosa("Cerrar sesión");
		btnSalir.setPreferredSize(new java.awt.Dimension(150, 38));
		btnSalir.addActionListener(e -> cerrarSesion());

		JPanel derecha = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 8));
		derecha.setOpaque(false);
		derecha.add(btnSalir);

		p.add(derecha, BorderLayout.EAST);

		return p;
	}

	// ====================== TARJETAS ======================

	private JPanel crearTarjetas() {

		JPanel grilla = new JPanel(new GridLayout(2, 3, 25, 25));
		grilla.setOpaque(false);
		grilla.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

		grilla.add(tarjeta("Materia Prima", "ovillo", () -> abrir("mp", MateriaPrimaVentana::new)));

		grilla.add(tarjeta("Orden de Producción", "orden", () -> abrir("orden", OrdenProduccionVentana::new)));

		grilla.add(tarjeta("Producto Terminado", "caja", () -> abrir("producto", ProductoTerminadoVentana::new)));

		grilla.add(tarjeta("Categoría", "etiqueta", () -> abrir("categoria", CategoriaProductoVentana::new)));

		grilla.add(tarjeta("Proveedor", "camion", () -> abrir("proveedor", ProveedorVentana::new)));

		grilla.add(tarjeta("Usuarios", "usuario", () -> abrir("usuario", UsuarioVentana::new)));

		return grilla;
	}

	private TarjetaBoton tarjeta(String texto, String icono, Runnable accion) {

		TarjetaBoton t = new TarjetaBoton(texto, icono);
		t.addActionListener(e -> accion.run());

		return t;
	}

	// ====================== MENU ======================

	private JMenuBar crearMenu() {

		JMenuBar barra = new JMenuBar();
		barra.setOpaque(true);
		barra.setBackground(ROSA_FUERTE);
		barra.setBorder(BorderFactory.createEmptyBorder());

		// MENÚ CONFIGURACIÓN

		JMenu mnConfig = menu("Configuración");

		mnConfig.add(item("Usuarios", "usuario", () -> abrir("usuario", UsuarioVentana::new)));

		mnConfig.addSeparator();

		mnConfig.add(item("Cerrar sesión", null, this::cerrarSesion));

		barra.add(mnConfig);

		// MENÚ REGISTROS

		JMenu mnRegistros = menu("Registros");

		mnRegistros.add(item("Materia Prima", "ovillo", () -> abrir("mp", MateriaPrimaVentana::new)));

		mnRegistros.add(item("Consumo de Materia Prima", "consumo", () -> abrir("orden", OrdenProduccionVentana::new)));

		mnRegistros.add(item("Orden de Producción", "orden", () -> abrir("orden", OrdenProduccionVentana::new)));

		mnRegistros.add(item("Proveedor", "camion", () -> abrir("proveedor", ProveedorVentana::new)));

		barra.add(mnRegistros);

		// MENÚ PRODUCTOS

		JMenu mnProductos = menu("Productos");

		mnProductos.add(
				item("Categoría de Producto", "etiqueta", () -> abrir("categoria", CategoriaProductoVentana::new)));

		mnProductos.add(item("Producto Terminado", "caja", () -> abrir("producto", ProductoTerminadoVentana::new)));

		barra.add(mnProductos);

		// MENÚ LISTADOS

		JMenu mnListados = menu("Listados");

		mnListados.add(item("Órdenes de Producción", "orden", () -> abrir("ordenes", OrdenesRegistradasVentana::new)));

		barra.add(mnListados);

		// MENÚ REPORTES

		JMenu mnReportes = menu("Reportes");

		mnReportes.add(
				item("Reporte de Materia Prima", "grafico", () -> abrir("rep_mp", ReporteMateriaPrimaVentana::new)));

		mnReportes.add(item("Reporte de Producto Terminado", "grafico",
				() -> abrir("rep_pt", ReporteProductoTerminadoVentana::new)));

		mnReportes.add(
				item("Reporte de Proveedores", "grafico", () -> abrir("rep_proveedor", ReporteProveedorVentana::new)));

		mnReportes.add(item("Reporte de Órdenes de Producción", "grafico",
				() -> abrir("rep_orden", ReporteOrdenProduccionVentana::new)));

		mnReportes.add(item("Reporte de Consumo de Materia Prima", "grafico",
				() -> abrir("rep_consumo", ReporteConsumoMateriaPrimaVentana::new)));

		// NUEVO REPORTE DE CATEGORÍAS DE PRODUCTOS

		mnReportes.add(item("Reporte de Categorías de Productos", "grafico",
				() -> abrir("rep_categoria", ReporteCategoriaProductoVentana::new)));

		barra.add(mnReportes);

		return barra;
	}

	private JMenu menu(String texto) {

		JMenu m = new JMenu(texto);
		m.setForeground(Color.WHITE);
		m.setFont(new Font("Segoe UI", Font.BOLD, 14));

		return m;
	}

	private JMenuItem item(String texto, String icono, Runnable accion) {

		JMenuItem it = new JMenuItem(texto);
		it.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		if (icono != null) {
			it.setIcon(Iconos.crear(icono, 18, ROSA_FUERTE));
		}

		it.addActionListener(e -> accion.run());

		return it;
	}

	// ====================== ACCIONES ======================

	private void abrir(String clave, Supplier<JFrame> fabrica) {

		JFrame v = abiertas.get(clave);

		if (v != null && v.isDisplayable()) {
			v.setState(JFrame.NORMAL);
			v.toFront();
			return;
		}

		v = fabrica.get();
		abiertas.put(clave, v);
		v.setVisible(true);
	}

	private void cerrarSesion() {

		int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);

		if (op != JOptionPane.YES_OPTION) {
			return;
		}

		for (JFrame v : abiertas.values()) {
			v.dispose();
		}

		abiertas.clear();

		SesionUsuario.cerrarSesion();

		dispose();

		new LoginVentana().setVisible(true);
	}

	private void salirAplicacion() {

		int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?", "Cerrar PretaSystem",
				JOptionPane.YES_NO_OPTION);

		if (opcion != JOptionPane.YES_OPTION) {
			return;
		}

		for (JFrame v : abiertas.values()) {
			v.dispose();
		}

		abiertas.clear();

		ConecctionHelper.cerrarSessionFactory();

		dispose();

		System.exit(0);
	}

	// ====================== COMPONENTES VISUALES ======================

	private static class FondoPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			g2.setPaint(new GradientPaint(0, 0, new Color(255, 245, 248), 0, h, new Color(250, 205, 222)));

			g2.fillRect(0, 0, w, h);

			g2.setColor(new Color(255, 255, 255, 120));
			g2.fillOval(-120, -120, 380, 380);
			g2.fillOval(w - 260, h - 240, 420, 420);

			g2.setColor(new Color(244, 143, 177, 45));
			g2.fillOval(w - 200, -90, 280, 280);
			g2.fillOval(-80, h - 200, 260, 260);

			g2.dispose();
		}
	}

	private static class TarjetaBoton extends JButton {

		private static final long serialVersionUID = 1L;

		private final String tipoIcono;

		TarjetaBoton(String texto, String tipoIcono) {

			super(texto);

			this.tipoIcono = tipoIcono;

			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setOpaque(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			boolean hover = getModel().isRollover() || getModel().isArmed();

			int w = getWidth() - 6;
			int h = getHeight() - 6;

			g2.setColor(new Color(173, 20, 87, 35));
			g2.fillRoundRect(4, 6, w, h, 32, 32);

			if (hover) {
				g2.setPaint(new GradientPaint(0, 0, ROSA_MEDIO, 0, h, ROSA_FUERTE));
			} else {
				g2.setColor(new Color(255, 255, 255, 240));
			}

			g2.fillRoundRect(0, 0, w, h, 32, 32);

			g2.setColor(hover ? ROSA_FUERTE : ROSA_MEDIO);
			g2.setStroke(new java.awt.BasicStroke(2f));
			g2.drawRoundRect(0, 0, w, h, 32, 32);

			int d = Math.min(90, Math.max(60, h / 2));
			int cx = (w - d) / 2;
			int cy = Math.max(12, (h - d - 40) / 2);

			g2.setColor(hover ? new Color(255, 255, 255, 70) : ROSA_CLARO);
			g2.fillOval(cx, cy, d, d);

			Icon ico = Iconos.crear(tipoIcono, (int) (d * 0.56), hover ? Color.WHITE : ROSA_FUERTE);

			ico.paintIcon(this, g2, cx + (d - ico.getIconWidth()) / 2, cy + (d - ico.getIconHeight()) / 2);

			g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
			g2.setColor(hover ? Color.WHITE : ROSA_OSCURO);

			FontMetrics fm = g2.getFontMetrics();

			g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2, cy + d + 28);

			g2.dispose();
		}
	}

	private static class BotonRosa extends JButton {

		private static final long serialVersionUID = 1L;

		BotonRosa(String texto) {

			super(texto);

			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setForeground(Color.WHITE);
			setFont(new Font("Segoe UI", Font.BOLD, 13));
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

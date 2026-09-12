package facitec.edu.py.pretasystem_abms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Color;

public class PantallaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PantallaPrincipal frame = new PantallaPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public PantallaPrincipal() {
		getContentPane().setBackground(new Color(255, 240, 245));
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\ojeda\\Documents\\Septimo semestre\\Seminario III\\ICONO\\fprincipal.png"));
		setTitle("PretaSystem - Trapillos BELA PRETA");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 680);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JButton btnMateriaPrima = new JButton("Materia Prima");
		btnMateriaPrima.setBackground(new Color(255, 128, 192));
		btnMateriaPrima.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMateriaPrima.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Materia Prima.png"));
		btnMateriaPrima.setBounds(10, 10, 149, 75);
		getContentPane().add(btnMateriaPrima);
		
		JButton btnProducto = new JButton("Producto");
		btnProducto.setBackground(new Color(255, 128, 192));
		btnProducto.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Producto.png"));
		btnProducto.setBounds(10, 95, 149, 81);
		getContentPane().add(btnProducto);
		
		JButton btnCategoria = new JButton("Categoria");
		btnCategoria.setBackground(new Color(255, 128, 192));
		btnCategoria.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Categoria.png"));
		btnCategoria.setBounds(10, 186, 149, 75);
		getContentPane().add(btnCategoria);
		
		JButton btnProveedor = new JButton("Proveedor");
		btnProveedor.setBackground(new Color(255, 128, 192));
		btnProveedor.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Proveedor.png"));
		btnProveedor.setBounds(10, 271, 149, 69);
		getContentPane().add(btnProveedor);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnConfiguración = new JMenu("Configuración");
		menuBar.add(mnConfiguración);
		
		JMenuItem mntmUsuarios = new JMenuItem("Usuarios");
		mnConfiguración.add(mntmUsuarios);
		
		JMenu mnRegistros = new JMenu("Registros");
		menuBar.add(mnRegistros);
		
		JMenuItem mntmMateriaPrima = new JMenuItem("Materia Prima");
		mnRegistros.add(mntmMateriaPrima);
		
		JMenuItem mntmConsumo = new JMenuItem("Consumo de Materia Prima");
		mnRegistros.add(mntmConsumo);
		
		JMenuItem mntmOrdenProducción = new JMenuItem("Orden de Producción");
		mnRegistros.add(mntmOrdenProducción);
		
		JMenuItem mntmProveedor = new JMenuItem("Proveedor");
		mnRegistros.add(mntmProveedor);
		
		JMenu mnProductos = new JMenu("Productos");
		menuBar.add(mnProductos);
		
		JMenuItem mntmCategoria = new JMenuItem("Categoria de Producto");
		mnProductos.add(mntmCategoria);
		
		JMenuItem mntmProductoTerminado = new JMenuItem("Producto Terminado");
		mnProductos.add(mntmProductoTerminado);
		
		JMenu mnReportes = new JMenu("Reportes");
		menuBar.add(mnReportes);
		
		JMenuItem mnReporteMateriaPrima = new JMenuItem("Reporte de Materia Prima");
		mnReportes.add(mnReporteMateriaPrima);
		
		JMenuItem mnReporteProductoTerminado = new JMenuItem("Reporte de Producto Terminado");
		mnReportes.add(mnReporteProductoTerminado);
		
		
	
		
		

	}
}

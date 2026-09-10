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
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\ojeda\\Documents\\Septimo semestre\\Seminario III\\ICONO\\fprincipal.png"));
		setTitle("PretaSystem - Trapillos BELA PRETA");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 680);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JButton btnMateriaPrima = new JButton("Materia Prima");
		btnMateriaPrima.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMateriaPrima.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Materia Prima.png"));
		btnMateriaPrima.setBounds(10, 10, 88, 54);
		getContentPane().add(btnMateriaPrima);
		
		JButton btnProducto = new JButton("Producto");
		btnProducto.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Producto.png"));
		btnProducto.setBounds(10, 67, 88, 54);
		getContentPane().add(btnProducto);
		
		JButton btnCategoria = new JButton("Categoria");
		btnCategoria.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Categoria.png"));
		btnCategoria.setBounds(10, 131, 88, 54);
		getContentPane().add(btnCategoria);
		
		JButton btnProveedor = new JButton("Proveedor");
		btnProveedor.setIcon(new ImageIcon("C:\\Users\\ojeda\\Documents\\Octavo Semestre\\Taller VIII\\Iconos\\Proveedor.png"));
		btnProveedor.setBounds(10, 195, 88, 54);
		getContentPane().add(btnProveedor);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMateriaPrima = new JMenu("Materia Prima");
		menuBar.add(mnMateriaPrima);
		
		JMenu mnNewMenu = new JMenu("Consumo Materia Prima");
		mnMateriaPrima.add(mnNewMenu);
		
		JMenu mnProducción = new JMenu("Orden de Producción");
		menuBar.add(mnProducción);
		
		JMenu mnProveedores = new JMenu("Proveedor");
		menuBar.add(mnProveedores);
		
		JMenu mnProducto = new JMenu("Producto Terminado");
		menuBar.add(mnProducto);
		
		JMenu mnCategoriaProducto = new JMenu("Categorias");
		menuBar.add(mnCategoriaProducto);
		
		
	
		
		

	}
}

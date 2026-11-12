
package facitec.edu.py.pretasystem_reportes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import facitec.edu.py.pretasystem_dao.Categoria_Producto_Dao;
import facitec.edu.py.pretasystem_entidades.Categoria_Producto;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Categoria_Producto;


public class ReporteCategoriaProductoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtBuscar;
	private JTable tablaCategorias;

	private Categoria_Producto_Dao dao;
	private Modelo_Tabla_Categoria_Producto modelo;

	public ReporteCategoriaProductoVentana() {

		dao = new Categoria_Producto_Dao();
		modelo = new Modelo_Tabla_Categoria_Producto();

		setTitle("Reporte de Categorías de Productos");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		setLocationRelativeTo(null);

		contentPane = new JPanel(new BorderLayout(10, 10));
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);

		JLabel lblTitulo = new JLabel("REPORTE DE CATEGORÍAS DE PRODUCTOS", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
		contentPane.add(lblTitulo, BorderLayout.NORTH);

		JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

		JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblBuscar = new JLabel("Buscar categoría:");
		txtBuscar = new JTextField(25);

		JButton btnBuscar = new JButton("Buscar");
		JButton btnMostrarTodas = new JButton("Mostrar todas");

		panelBusqueda.add(lblBuscar);
		panelBusqueda.add(txtBuscar);
		panelBusqueda.add(btnBuscar);
		panelBusqueda.add(btnMostrarTodas);

		panelCentral.add(panelBusqueda, BorderLayout.NORTH);

		tablaCategorias = new JTable(modelo);
		tablaCategorias.setRowHeight(25);
		tablaCategorias.setFont(new Font("Arial", Font.PLAIN, 13));
		tablaCategorias.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

		JScrollPane scrollPane = new JScrollPane(tablaCategorias);
		panelCentral.add(scrollPane, BorderLayout.CENTER);

		contentPane.add(panelCentral, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton btnGenerarReporte = new JButton("Imprimir");
		JButton btnCerrar = new JButton("Cerrar");

		panelBotones.add(btnGenerarReporte);
		panelBotones.add(btnCerrar);

		contentPane.add(panelBotones, BorderLayout.SOUTH);

		btnBuscar.addActionListener(e -> buscarCategorias());

		btnMostrarTodas.addActionListener(e -> {
			txtBuscar.setText("");
			cargarCategorias();
		});

		txtBuscar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				buscarCategorias();
			}
		});

		btnGenerarReporte.addActionListener(e -> generarReporte());

		btnCerrar.addActionListener(e -> dispose());

		cargarCategorias();
	}

	private void cargarCategorias() {
		try {
			List<Categoria_Producto> lista = dao.listarParaReporteCategoriaProducto();

			modelo.setDatos(lista);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "No se pudieron cargar las categorías.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buscarCategorias() {
		try {
			String filtro = txtBuscar.getText().trim();

			if (filtro.isEmpty()) {
				cargarCategorias();
				return;
			}

			List<Categoria_Producto> lista = dao.recuperarPorFiltro(filtro);
			modelo.setDatos(lista);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error al buscar las categorías.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void generarReporte() {
		try {
			ReporteUtil.mostrar(this, "reporte_categoria_producto.jrxml", "Reporte de Categorías de Productos",
					modelo.getDatos(), null);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "No se pudo generar el reporte.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			ReporteCategoriaProductoVentana ventana = new ReporteCategoriaProductoVentana();
			ventana.setVisible(true);
		});
	}
}

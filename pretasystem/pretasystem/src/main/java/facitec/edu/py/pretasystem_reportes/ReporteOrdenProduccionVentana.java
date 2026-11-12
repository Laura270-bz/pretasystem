
package facitec.edu.py.pretasystem_reportes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

import facitec.edu.py.pretasystem_dao.Orden_Produccion_Dao;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Orden_Produccion;

public class ReporteOrdenProduccionVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private Orden_Produccion_Dao dao;
	private Modelo_Tabla_Orden_Produccion modelo;
	private JTable tabla;

	private JTextField txtBuscar;
	private JButton btnBuscar;
	private JButton btnGenerar;
	private JButton btnCerrar;

	public ReporteOrdenProduccionVentana() {

		dao = new Orden_Produccion_Dao();
		modelo = new Modelo_Tabla_Orden_Produccion();

		configurarVentana();
		inicializarComponentes();
		cargarOrdenes();
	}

	private void configurarVentana() {

		setTitle("Reporte de Órdenes de Producción");
		setSize(1100, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));
	}

	private void inicializarComponentes() {

		// TÍTULO
		JLabel lblTitulo = new JLabel("REPORTE DE ÓRDENES DE PRODUCCIÓN", SwingConstants.CENTER);

		lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));

		add(lblTitulo, BorderLayout.NORTH);

		// PANEL DE BÚSQUEDA
		JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

		JLabel lblBuscar = new JLabel("Buscar por etapa:");
		lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));

		txtBuscar = new JTextField(25);

		btnBuscar = new JButton("Buscar");

		panelBusqueda.add(lblBuscar);
		panelBusqueda.add(txtBuscar);
		panelBusqueda.add(btnBuscar);

		// TABLA
		tabla = new JTable(modelo);
		tabla.setRowHeight(25);
		tabla.setFont(new Font("Arial", Font.PLAIN, 12));

		JTableHeader encabezado = tabla.getTableHeader();
		encabezado.setFont(new Font("Arial", Font.BOLD, 12));

		JScrollPane scroll = new JScrollPane(tabla);

		JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
		panelCentral.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		panelCentral.add(panelBusqueda, BorderLayout.NORTH);
		panelCentral.add(scroll, BorderLayout.CENTER);

		add(panelCentral, BorderLayout.CENTER);

		// BOTONES INFERIORES
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		btnGenerar = new JButton("Imprimir");
		btnCerrar = new JButton("Cerrar");

		panelBotones.add(btnGenerar);
		panelBotones.add(btnCerrar);

		add(panelBotones, BorderLayout.SOUTH);

		// EVENTOS
		btnBuscar.addActionListener(e -> buscarOrdenes());

		txtBuscar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					buscarOrdenes();
				}
			}
		});

		btnGenerar.addActionListener(e -> generarReporte());

		btnCerrar.addActionListener(e -> dispose());
	}

	private void cargarOrdenes() {

		try {
			List<Orden_Produccion> lista = dao.listarParaReporteOrdenProduccion();

			modelo.setDatos(lista);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error al cargar las órdenes de producción:\n" + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buscarOrdenes() {

		try {

			String filtro = txtBuscar.getText().trim();

			List<Orden_Produccion> lista;

			if (filtro.isEmpty()) {
				lista = dao.listarParaReporteOrdenProduccion();
			} else {
				lista = dao.recuperarPorFiltro(filtro);
			}

			modelo.setDatos(lista);

			if (lista == null || lista.isEmpty()) {
				JOptionPane.showMessageDialog(this, "No se encontraron órdenes de producción.", "Búsqueda",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error al buscar las órdenes:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void generarReporte() {

		try {

			if (modelo.getDatos() == null || modelo.getDatos().isEmpty()) {

				JOptionPane.showMessageDialog(this, "No hay órdenes de producción para generar el reporte.", "Aviso",
						JOptionPane.WARNING_MESSAGE);

				return;
			}

			ReporteUtil.mostrar(this, "reporte_orden_produccion.jrxml", "Reporte de Órdenes de Producción",
					modelo.getDatos(), null);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error al generar el reporte:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}

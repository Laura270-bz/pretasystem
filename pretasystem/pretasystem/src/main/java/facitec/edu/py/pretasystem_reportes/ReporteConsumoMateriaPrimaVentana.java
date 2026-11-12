
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
import javax.swing.SwingUtilities;

import facitec.edu.py.pretasystem_dao.Consumo_Materia_Prima_Dao;
import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Consumo_Materia_Prima;

public class ReporteConsumoMateriaPrimaVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private Consumo_Materia_Prima_Dao dao;
	private Modelo_Tabla_Consumo_Materia_Prima modelo;

	private JTable tabla;
	private JTextField txtBuscar;

	private JButton btnBuscar;
	private JButton btnGenerarReporte;
	private JButton btnCerrar;

	public ReporteConsumoMateriaPrimaVentana() {

		dao = new Consumo_Materia_Prima_Dao();
		modelo = new Modelo_Tabla_Consumo_Materia_Prima();

		configurarVentana();
		inicializarComponentes();
		cargarConsumos();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void configurarVentana() {

		setTitle("Reporte de Consumo de Materia Prima");
		setSize(1050, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));
	}

	private void inicializarComponentes() {

		// ENCABEZADO
		JPanel panelTitulo = new JPanel(new BorderLayout());
		panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		JLabel lblTitulo = new JLabel("REPORTE DE CONSUMO DE MATERIA PRIMA", SwingConstants.CENTER);

		lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

		panelTitulo.add(lblTitulo, BorderLayout.CENTER);

		add(panelTitulo, BorderLayout.NORTH);

		// PANEL DE BUSQUEDA
		JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

		JLabel lblBuscar = new JLabel("Buscar por materia prima o etapa:");

		txtBuscar = new JTextField(25);

		btnBuscar = new JButton("Buscar");

		panelBuscar.add(lblBuscar);
		panelBuscar.add(txtBuscar);
		panelBuscar.add(btnBuscar);

		// TABLA
		tabla = new JTable(modelo);
		tabla.setRowHeight(25);
		tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

		JScrollPane scroll = new JScrollPane(tabla);

		JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
		panelCentro.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		panelCentro.add(panelBuscar, BorderLayout.NORTH);
		panelCentro.add(scroll, BorderLayout.CENTER);

		add(panelCentro, BorderLayout.CENTER);

		// BOTONES INFERIORES
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));

		btnGenerarReporte = new JButton("Imprimir");
		btnCerrar = new JButton("Cerrar");

		panelBotones.add(btnGenerarReporte);
		panelBotones.add(btnCerrar);

		add(panelBotones, BorderLayout.SOUTH);

		// EVENTOS
		btnBuscar.addActionListener(e -> buscarConsumos());

		txtBuscar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				buscarConsumos();
			}
		});

		btnGenerarReporte.addActionListener(e -> generarReporte());

		btnCerrar.addActionListener(e -> dispose());
	}

	/**
	 * Carga todos los consumos registrados.
	 */
	private void cargarConsumos() {

		try {

			List<Consumo_Materia_Prima> lista = dao.listarParaReporteConsumoMateriaPrima();

			modelo.setDatos(lista);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this,
					"No se pudieron cargar los consumos de materia prima.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Busca consumos por nombre de materia prima o etapa de produccion.
	 */
	private void buscarConsumos() {

		try {

			String filtro = txtBuscar.getText().trim();

			List<Consumo_Materia_Prima> lista = dao.recuperarPorFiltro(filtro);

			modelo.setDatos(lista);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Error al buscar los consumos.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Genera el reporte utilizando JasperReports.
	 */
	private void generarReporte() {

		try {

			if (modelo.getDatos().isEmpty()) {

				JOptionPane.showMessageDialog(this, "No hay consumos para generar el reporte.", "Aviso",
						JOptionPane.WARNING_MESSAGE);

				return;
			}

			ReporteUtil.mostrar(this, "reporte_consumo_materia_prima.jrxml", "Reporte de Consumo de Materia Prima",
					modelo.getDatos(), null);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "No se pudo generar el reporte.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> new ReporteConsumoMateriaPrimaVentana());
	}
}

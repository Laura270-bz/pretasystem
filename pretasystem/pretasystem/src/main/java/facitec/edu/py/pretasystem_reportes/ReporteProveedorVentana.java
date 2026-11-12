
package facitec.edu.py.pretasystem_reportes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import facitec.edu.py.pretasystem_dao.Proveedor_Dao;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Proveedor;

public class ReporteProveedorVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Proveedor_Dao dao = new Proveedor_Dao();

	private final Modelo_Tabla_Proveedor modelo = new Modelo_Tabla_Proveedor();

	private final JTable tabla = new JTable(modelo);

	private final JTextField txtFiltro = new JTextField(20);

	public ReporteProveedorVentana() {

		setTitle("Reporte de Proveedores");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setSize(950, 500);

		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout(10, 10));

		// PARTE SUPERIOR: BÚSQUEDA

		JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));

		norte.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		norte.add(new JLabel("Nombre o documento:"));

		norte.add(txtFiltro);

		JButton btnBuscar = new JButton("Buscar");

		norte.add(btnBuscar);

		getContentPane().add(norte, BorderLayout.NORTH);

		// CENTRO: TABLA

		tabla.getColumnModel().getColumn(0).setMaxWidth(60);

		JScrollPane scroll = new JScrollPane(tabla);

		scroll.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		getContentPane().add(scroll, BorderLayout.CENTER);

		// PARTE INFERIOR: BOTONES

		JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		sur.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

		JButton btnGenerar = new JButton("Imprimir");

		JButton btnCerrar = new JButton("Cerrar");

		sur.add(btnGenerar);

		sur.add(btnCerrar);

		getContentPane().add(sur, BorderLayout.SOUTH);

		// EVENTOS

		btnBuscar.addActionListener(e -> cargar());

		txtFiltro.addActionListener(e -> cargar());

		btnGenerar.addActionListener(e -> generarReporte());

		btnCerrar.addActionListener(e -> dispose());

		// CARGA INICIAL

		cargar();
	}

	/**
	 * Carga los proveedores en la tabla.
	 */
	private void cargar() {

		try {

			String filtro = txtFiltro.getText().trim();

			if (filtro.isEmpty()) {

				modelo.setDatos(dao.listarParaReporteProveedor());

			} else {

				modelo.setDatos(dao.recuperarPorFiltro(filtro));
			}

		} catch (Exception ex) {

			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, "Error al cargar los proveedores: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Genera el reporte con los registros visibles en la tabla.
	 */
	private void generarReporte() {

		if (modelo.getDatos().isEmpty()) {

			JOptionPane.showMessageDialog(this, "No hay proveedores para generar el reporte.");

			return;
		}

		ReporteUtil.mostrar(this, "reporte_proveedor.jrxml", "Reporte de Proveedores", modelo.getDatos(), null);
	}
}

package facitec.edu.py.pretasystem_reportes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Comparator;
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

import facitec.edu.py.pretasystem_dao.Producto_Terminado_Dao;
import facitec.edu.py.pretasystem_entidades.Producto_Terminado;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Producto_Terminado;

public class ReporteProductoTerminadoVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Producto_Terminado_Dao dao = new Producto_Terminado_Dao();
	private final Modelo_Tabla_Producto_Terminado modelo = new Modelo_Tabla_Producto_Terminado();
	private final JTable tabla = new JTable(modelo);
	private final JTextField txtFiltro = new JTextField(20);

	public ReporteProductoTerminadoVentana() {
		setTitle("Reporte de Producto Terminado");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(900, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(10, 10));

		// Parte superior: búsqueda por nombre
		JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
		norte.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		norte.add(new JLabel("Nombre:"));
		norte.add(txtFiltro);
		JButton btnBuscar = new JButton("Buscar");
		norte.add(btnBuscar);
		getContentPane().add(norte, BorderLayout.NORTH);

		// Centro: tabla
		tabla.getColumnModel().getColumn(0).setMaxWidth(70);
		JScrollPane scroll = new JScrollPane(tabla);
		scroll.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		getContentPane().add(scroll, BorderLayout.CENTER);

		// Parte inferior: botones
		JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		sur.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
		JButton btnImprimir = new JButton("Imprimir");
		JButton btnCerrar = new JButton("Cerrar");
		sur.add(btnImprimir);
		sur.add(btnCerrar);
		getContentPane().add(sur, BorderLayout.SOUTH);

		// Eventos
		btnBuscar.addActionListener(e -> cargar());
		txtFiltro.addActionListener(e -> cargar());
		btnImprimir.addActionListener(e -> imprimir());
		btnCerrar.addActionListener(e -> dispose());

		cargar();
	}

	private void cargar() {
		try {
			modelo.setDatos(dao.recuperarPorFiltro(txtFiltro.getText().trim()));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void imprimir() {
		if (modelo.getDatos().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay datos para imprimir.");
			return;
		}

		// El reporte agrupa por categoría, así que los datos deben ir ordenados por
		// categoría
		List<Producto_Terminado> ordenada = new ArrayList<>(modelo.getDatos());
		ordenada.sort(Comparator
				.comparing((Producto_Terminado p) -> p.getCategoria_producto().getNombre_categoria(),
						String.CASE_INSENSITIVE_ORDER)
				.thenComparing(Producto_Terminado::getNombre, String.CASE_INSENSITIVE_ORDER));

		ReporteUtil.mostrar(this, "reporte_producto_terminado.jrxml", "Reporte de Producto Terminado", ordenada, null);
	}
}
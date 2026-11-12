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

import facitec.edu.py.pretasystem_dao.Materia_Prima_Dao;
import facitec.edu.py.pretasystem_modelo_tabla.Modelo_Tabla_Materia_Prima;
import facitec.edu.py.pretasystem_reportes.ReporteUtil;

public class ReporteMateriaPrimaVentana extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Materia_Prima_Dao dao = new Materia_Prima_Dao();
	private final Modelo_Tabla_Materia_Prima modelo = new Modelo_Tabla_Materia_Prima();
	private final JTable tabla = new JTable(modelo);
	private final JTextField txtFiltro = new JTextField(20);

	public ReporteMateriaPrimaVentana() {
		setTitle("Reporte de Materia Prima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(750, 480);
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
		txtFiltro.addActionListener(e -> cargar()); // Enter en el campo también busca
		btnImprimir.addActionListener(e -> imprimir());
		btnCerrar.addActionListener(e -> dispose());

		cargar(); // al abrir muestra todos los registros
	}

	// Trae los datos del DAO y los muestra en la tabla
	private void cargar() {
		try {
			modelo.setDatos(dao.recuperarPorFiltro(txtFiltro.getText().trim()));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Imprime exactamente lo que se ve en la tabla
	private void imprimir() {
		if (modelo.getDatos().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay datos para imprimir.");
			return;
		}
		ReporteUtil.mostrar(this, "reporte_materia_prima.jrxml", "Reporte de Materia Prima",
				modelo.getDatos(), null);
	}
}

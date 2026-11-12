package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Producto_Terminado;

public class Modelo_Tabla_Producto_Terminado extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNAS = { "ID", "Nombre", "Color", "Categoría", "Stock actual", "Stock mínimo",
			"Precio minorista", "Precio mayorista" };

	private List<Producto_Terminado> datos = new ArrayList<>();

	public void setDatos(List<Producto_Terminado> nuevos) {
		this.datos = new ArrayList<>(nuevos);
		fireTableDataChanged();
	}

	public List<Producto_Terminado> getDatos() {
		return datos;
	}

	@Override
	public int getRowCount() {
		return datos.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNAS.length;
	}

	@Override
	public String getColumnName(int columna) {
		return COLUMNAS[columna];
	}

	@Override
	public Class<?> getColumnClass(int columna) {
		switch (columna) {
		case 0:
		case 4:
		case 5:
		case 6:
		case 7:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Producto_Terminado p = datos.get(fila);
		switch (columna) {
		case 0:
			return p.getId_producto_terminado();
		case 1:
			return p.getNombre();
		case 2:
			return p.getColor();
		case 3:
			return p.getCategoria_producto() == null ? "" : p.getCategoria_producto().getNombre_categoria();
		case 4:
			return p.getStock_actual();
		case 5:
			return p.getStock_minimo();
		case 6:
			return p.getPrecio_minorista();
		case 7:
			return p.getPrecio_mayorista();
		default:
			return null;
		}
	}
}
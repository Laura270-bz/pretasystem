
package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Categoria_Producto;

public class Modelo_Tabla_Categoria_Producto extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final String[] columnas = { "ID Categoría", "Nombre de la categoría" };

	private List<Categoria_Producto> datos = new ArrayList<>();

	public void setDatos(List<Categoria_Producto> datos) {
		this.datos = datos != null ? datos : new ArrayList<>();
		fireTableDataChanged();
	}

	public List<Categoria_Producto> getDatos() {
		return datos;
	}

	@Override
	public int getRowCount() {
		return datos.size();
	}

	@Override
	public int getColumnCount() {
		return columnas.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnas[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		default:
			return Object.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Categoria_Producto categoria = datos.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return categoria.getId_categoria_producto();
		case 1:
			return categoria.getNombre_categoria();
		default:
			return null;
		}
	}
}

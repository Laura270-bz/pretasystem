package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Materia_Prima;

public class Modelo_Tabla_Materia_Prima extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNAS = { "ID", "Nombre", "Stock actual", "Stock mínimo", "Unidad de medida" };

	private List<Materia_Prima> datos = new ArrayList<>();

	public void setDatos(List<Materia_Prima> nuevos) {
		this.datos = new ArrayList<>(nuevos);
		fireTableDataChanged();
	}

	public List<Materia_Prima> getDatos() {
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
		return (columna == 0 || columna == 2 || columna == 3) ? Integer.class : String.class;
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Materia_Prima m = datos.get(fila);
		switch (columna) {
		case 0:
			return m.getId_materia_prima();
		case 1:
			return m.getNombre();
		case 2:
			return m.getStock_actual();
		case 3:
			return m.getStock_minimo();
		case 4:
			return m.getUnidad_medida();
		default:
			return null;
		}
	}
}
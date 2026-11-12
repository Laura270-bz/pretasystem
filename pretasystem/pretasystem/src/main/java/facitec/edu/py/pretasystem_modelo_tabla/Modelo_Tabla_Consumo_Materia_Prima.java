
package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;

public class Modelo_Tabla_Consumo_Materia_Prima extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNAS = { "ID Consumo", "Materia prima", "Cantidad utilizada", "ID Orden",
			"Etapa actual" };

	private List<Consumo_Materia_Prima> datos = new ArrayList<>();

	public void setDatos(List<Consumo_Materia_Prima> nuevos) {
		this.datos = new ArrayList<>(nuevos);
		fireTableDataChanged();
	}

	public List<Consumo_Materia_Prima> getDatos() {
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
		case 2:
		case 3:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int fila, int columna) {

		Consumo_Materia_Prima c = datos.get(fila);

		switch (columna) {

		case 0:
			return c.getId_consumo_materia_prima();

		case 1:
			return c.getMateria_prima() == null ? "" : c.getMateria_prima().getNombre();

		case 2:
			return c.getCantidad_usada();

		case 3:
			return c.getOrden_produccion() == null ? 0 : c.getOrden_produccion().getId_orden_produccion();

		case 4:
			return c.getOrden_produccion() == null ? "" : c.getOrden_produccion().getEtapa_actual();

		default:
			return null;
		}
	}
}

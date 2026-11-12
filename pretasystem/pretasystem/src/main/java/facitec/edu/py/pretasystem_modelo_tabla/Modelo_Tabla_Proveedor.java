
package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Proveedor;

public class Modelo_Tabla_Proveedor extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNAS = { "ID", "Documento", "Nombre", "Dirección", "Teléfono", "Email" };

	private List<Proveedor> datos = new ArrayList<>();

	public void setDatos(List<Proveedor> nuevos) {
		this.datos = new ArrayList<>(nuevos);
		fireTableDataChanged();
	}

	public List<Proveedor> getDatos() {
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
		return columna == 0 ? Integer.class : String.class;
	}

	@Override
	public Object getValueAt(int fila, int columna) {

		Proveedor p = datos.get(fila);

		switch (columna) {

		case 0:
			return p.getId_proveedores();

		case 1:
			return p.getDocumento();

		case 2:
			return p.getNombre();

		case 3:
			return p.getDireccion();

		case 4:
			return p.getTelefono();

		case 5:
			return p.getEmail();

		default:
			return null;
		}
	}
}

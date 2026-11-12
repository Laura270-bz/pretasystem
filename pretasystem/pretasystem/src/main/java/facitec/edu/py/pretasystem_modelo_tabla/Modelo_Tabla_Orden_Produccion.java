
package facitec.edu.py.pretasystem_modelo_tabla;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import facitec.edu.py.pretasystem_entidades.Orden_Produccion;

public class Modelo_Tabla_Orden_Produccion extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNAS = { "ID", "Fecha de inicio", "Fecha de fin", "Cantidad estimada",
			"Cantidad real", "Cantidad pendiente", "Etapa actual", "Estado", "Usuario responsable" };

	private List<Orden_Produccion> datos = new ArrayList<>();

	/**
	 * Actualiza los datos de la tabla.
	 */
	public void setDatos(List<Orden_Produccion> nuevos) {
		datos = nuevos == null ? new ArrayList<>() : new ArrayList<>(nuevos);

		fireTableDataChanged();
	}

	/**
	 * Devuelve la lista de órdenes cargadas.
	 */
	public List<Orden_Produccion> getDatos() {
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
		case 3:
		case 4:
		case 5:
			return Integer.class;

		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int fila, int columna) {

		Orden_Produccion o = datos.get(fila);

		switch (columna) {

		case 0:
			return o.getId_orden_produccion();

		case 1:
			return o.getFecha_inicio() == null ? "" : o.getFecha_inicio().toString();

		case 2:
			return o.getFecha_fin() == null ? "Sin finalizar" : o.getFecha_fin().toString();

		case 3:
			return o.getCantidad_estimada();

		case 4:
			return o.getProducida();

		case 5:
			return o.getPendiente();

		case 6:
			return o.getEtapa_actual();

		case 7:
			return o.getEstado();

		case 8:
			return o.getUsuario() == null ? "No asignado" : o.getUsuario().getUsuario();

		default:
			return null;
		}
	}
}

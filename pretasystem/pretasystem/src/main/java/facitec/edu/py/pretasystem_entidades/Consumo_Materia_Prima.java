package facitec.edu.py.pretasystem_entidades;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consumo_materia_prima")
public class Consumo_Materia_Prima {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_consumo_materia_prima;

	@Column(nullable = false)
	private int cantidad_usada; // CORREGIDO: era String, ahora numerico para poder restar stock

	// FALTABAN las relaciones (las FK del diagrama)
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_materia_prima", nullable = false)
	private Materia_Prima materia_prima;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_orden_produccion", nullable = false)
	private Orden_Produccion orden_produccion;

	public Consumo_Materia_Prima() {
	}

	public int getId_consumo_materia_prima() {
		return id_consumo_materia_prima;
	}

	public void setId_consumo_materia_prima(int id_consumo_materia_prima) {
		this.id_consumo_materia_prima = id_consumo_materia_prima;
	}

	public int getCantidad_usada() {
		return cantidad_usada;
	}

	public void setCantidad_usada(int cantidad_usada) {
		this.cantidad_usada = cantidad_usada;
	}

	public Materia_Prima getMateria_prima() {
		return materia_prima;
	}

	public void setMateria_prima(Materia_Prima materia_prima) {
		this.materia_prima = materia_prima;
	}

	public Orden_Produccion getOrden_produccion() {
		return orden_produccion;
	}

	public void setOrden_produccion(Orden_Produccion orden_produccion) {
		this.orden_produccion = orden_produccion;
	}

	@Override
	public String toString() {
		return "Consumo_Materia_Prima [id=" + id_consumo_materia_prima + ", cantidad_usada=" + cantidad_usada + "]";
	}
}
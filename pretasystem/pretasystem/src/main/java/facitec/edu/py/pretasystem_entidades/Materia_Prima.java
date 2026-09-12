package facitec.edu.py.pretasystem_entidades;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "materia_prima")
public class Materia_Prima {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_materia_prima;
	@Column(nullable = false, length = 255)
	private String nombre;
	@Column(nullable = false, length = 255)
	private int stock_actual;
	@Column(nullable = false, length = 255)
	private int stock_minimo;
	@Column(nullable = false, length = 255)
	private String unidad_medida;

	

	public Materia_Prima() {
		// TODO Auto-generated constructor stub

}



	public Materia_Prima(int id_materia_prima, String nombre, int stock_actual, int stock_minimo,
			String unidad_medida) {
		super();
		this.id_materia_prima = id_materia_prima;
		this.nombre = nombre;
		this.stock_actual = stock_actual;
		this.stock_minimo = stock_minimo;
		this.unidad_medida = unidad_medida;
	}



	public int getId_materia_prima() {
		return id_materia_prima;
	}



	public void setId_materia_prima(int id_materia_prima) {
		this.id_materia_prima = id_materia_prima;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public int getStock_actual() {
		return stock_actual;
	}



	public void setStock_actual(int stock_actual) {
		this.stock_actual = stock_actual;
	}



	public int getStock_minimo() {
		return stock_minimo;
	}



	public void setStock_minimo(int stock_minimo) {
		this.stock_minimo = stock_minimo;
	}



	public String getUnidad_medida() {
		return unidad_medida;
	}



	public void setUnidad_medida(String unidad_medida) {
		this.unidad_medida = unidad_medida;
	}



	@Override
	public String toString() {
		return "Materia_Prima [id_materia_prima=" + id_materia_prima + ", nombre=" + nombre + ", stock_actual="
				+ stock_actual + ", stock_minimo=" + stock_minimo + ", unidad_medida=" + unidad_medida + "]";
	}
	
}

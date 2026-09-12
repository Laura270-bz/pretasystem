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
@Table(name = "consumo_materia_prima")
public class Consumo_Materia_Prima {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_consumo_materia_prima;
	@Column(nullable = false, length = 255)
	private String cantidad_usada;

	

	public Consumo_Materia_Prima() {
		// TODO Auto-generated constructor stub
	}



	public Consumo_Materia_Prima(int id_consumo_materia_prima, String cantidad_usada) {
		super();
		this.id_consumo_materia_prima = id_consumo_materia_prima;
		this.cantidad_usada = cantidad_usada;
	}



	public int getId_consumo_materia_prima() {
		return id_consumo_materia_prima;
	}



	public void setId_consumo_materia_prima(int id_consumo_materia_prima) {
		this.id_consumo_materia_prima = id_consumo_materia_prima;
	}



	public String getCantidad_usada() {
		return cantidad_usada;
	}



	public void setCantidad_usada(String cantidad_usada) {
		this.cantidad_usada = cantidad_usada;
	}



	@Override
	public String toString() {
		return "Consumo_Materia_Prima [id_consumo_materia_prima=" + id_consumo_materia_prima + ", cantidad_usada="
				+ cantidad_usada + "]";
	}

	
	



	

}

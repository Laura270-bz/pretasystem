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

	@ManyToOne
	private Consumo_Materia_Prima consumo_Materia_Prima;

	public Consumo_Materia_Prima() {
		// TODO Auto-generated constructor stub
	}

	public Consumo_Materia_Prima(int id_consumo_materia_prima, String cantidad_usada,
			Consumo_Materia_Prima consumo_Materia_Prima) {
		super();
		this.id_consumo_materia_prima = id_consumo_materia_prima;
		this.cantidad_usada = cantidad_usada;
		this.consumo_Materia_Prima = consumo_Materia_Prima;
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

	public List<Materia_Prima> getMateria_Primas() {
		return materia_Primas;
	}

	public void setMateria_Primas(List<Materia_Prima> materia_Primas) {
		this.materia_Primas = materia_Primas;
	}

	@Override
	public String toString() {
		return "Consumo_Materia_Prima [id_consumo_materia_prima=" + id_consumo_materia_prima + ", cantidad_usada="
				+ cantidad_usada + ", materia_Primas=" + materia_Primas + "]";
	}

}

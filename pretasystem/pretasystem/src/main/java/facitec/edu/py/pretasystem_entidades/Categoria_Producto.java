package facitec.edu.py.pretasystem_entidades;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria_producto")
public class Categoria_Producto {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_categoria_producto;
	@Column(nullable = false, length = 255)
	private String nombre_categoria;

	public Categoria_Producto() {
		// TODO Auto-generated constructor stub
	}

	public Categoria_Producto(int id_categoria_producto, String nombre_categoria) {
		super();
		this.id_categoria_producto = id_categoria_producto;
		this.nombre_categoria = nombre_categoria;
	}

	public int getId_categoria_producto() {
		return id_categoria_producto;
	}

	public void setId_categoria_producto(int id_categoria_producto) {
		this.id_categoria_producto = id_categoria_producto;
	}

	public String getNombre_categoria() {
		return nombre_categoria;
	}

	public void setNombre_categoria(String nombre_categoria) {
		this.nombre_categoria = nombre_categoria;
	}

	@Override
	public String toString() {
		return "Categoria_Producto [id_categoria_producto=" + id_categoria_producto + ", nombre_categoria="
				+ nombre_categoria + "]";
	}

}

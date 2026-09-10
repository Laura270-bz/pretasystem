package facitec.edu.py.pretasystem_entidades;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_terminado")
public class Producto_Terminado {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_producto_terminado;
	@Column(nullable = false, length = 255)
	private String nombre;
	@Column(nullable = false, length = 255)
	private String color;
	@Column(nullable = false, length = 255)
	private int stock_actual;
	private int stock_minimo;
	private int precio_minorista;
	private int precio_mayorista;

	public Producto_Terminado() {
		// TODO Auto-generated constructor stub
	}

	public Producto_Terminado(int id_producto_terminado, String nombre, String color, int stock_actual,
			int stock_minimo, int precio_minorista, int precio_mayorista) {
		super();
		this.id_producto_terminado = id_producto_terminado;
		this.nombre = nombre;
		this.color = color;
		this.stock_actual = stock_actual;
		this.stock_minimo = stock_minimo;
		this.precio_minorista = precio_minorista;
		this.precio_mayorista = precio_mayorista;
	}

	public int getId_producto_terminado() {
		return id_producto_terminado;
	}

	public void setId_producto_terminado(int id_producto_terminado) {
		this.id_producto_terminado = id_producto_terminado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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

	public int getPrecio_minorista() {
		return precio_minorista;
	}

	public void setPrecio_minorista(int precio_minorista) {
		this.precio_minorista = precio_minorista;
	}

	public int getPrecio_mayorista() {
		return precio_mayorista;
	}

	public void setPrecio_mayorista(int precio_mayorista) {
		this.precio_mayorista = precio_mayorista;
	}

	@Override
	public String toString() {
		return "Producto_Terminado [id_producto_terminado=" + id_producto_terminado + ", nombre=" + nombre + ", color="
				+ color + ", stock_actual=" + stock_actual + ", stock_minimo=" + stock_minimo + ", precio_minorista="
				+ precio_minorista + ", precio_mayorista=" + precio_mayorista + "]";
	}

}

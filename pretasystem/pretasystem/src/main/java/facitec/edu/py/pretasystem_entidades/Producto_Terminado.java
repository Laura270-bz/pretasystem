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

	@Column(nullable = false)
	private int stock_actual;

	@Column(nullable = false)
	private int stock_minimo;

	@Column(nullable = false)
	private int precio_minorista;

	@Column(nullable = false)
	private int precio_mayorista;

	// RELACIONES (FK del diagrama) 
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_categoria_producto", nullable = false)
	private Categoria_Producto categoria_producto;

	@ManyToOne
	@JoinColumn(name = "id_orden_produccion", nullable = true)
	private Orden_Produccion orden_produccion;

	public Producto_Terminado() {
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

	public Categoria_Producto getCategoria_producto() {
		return categoria_producto;
	}

	public void setCategoria_producto(Categoria_Producto categoria_producto) {
		this.categoria_producto = categoria_producto;
	}

	public Orden_Produccion getOrden_produccion() {
		return orden_produccion;
	}

	public void setOrden_produccion(Orden_Produccion orden_produccion) {
		this.orden_produccion = orden_produccion;
	}

	@Override
	public String toString() {
		return "Producto_Terminado [id=" + id_producto_terminado + ", nombre=" + nombre + ", color=" + color
				+ ", stock_actual=" + stock_actual + ", stock_minimo=" + stock_minimo + ", precio_minorista="
				+ precio_minorista + ", precio_mayorista=" + precio_mayorista + "]";
	}
}
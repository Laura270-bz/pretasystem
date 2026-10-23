package facitec.edu.py.pretasystem_entidades;

import java.sql.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orden_produccion")
public class Orden_Produccion {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_orden_produccion;

	@Column(nullable = false)
	private Date fecha_inicio;

	@Column(nullable = true) // se completa al cerrar la orden
	private Date fecha_fin;

	@Column(nullable = false)
	private int cantidad_estimada; // CORREGIDO: nombre (tenia typo) y tipo numerico

	@Column(nullable = true) // se completa al cerrar la orden
	private Integer cantidad_real;

	@Column(nullable = false, length = 255)
	private String etapa_actual; // Corte, Limpieza, Ovillado, Bobinado, Sellado

	// Supervisor responsable. Si todavia no tenes la clase Usuario, comenta estas 2 lineas
	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = true)
	private Usuario usuario;

	public Orden_Produccion() {
	}

	public int getId_orden_produccion() {
		return id_orden_produccion;
	}

	public void setId_orden_produccion(int id_orden_produccion) {
		this.id_orden_produccion = id_orden_produccion;
	}

	public Date getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(Date fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public Date getFecha_fin() {
		return fecha_fin;
	}

	public void setFecha_fin(Date fecha_fin) {
		this.fecha_fin = fecha_fin;
	}

	public int getCantidad_estimada() {
		return cantidad_estimada;
	}

	public void setCantidad_estimada(int cantidad_estimada) {
		this.cantidad_estimada = cantidad_estimada;
	}

	public Integer getCantidad_real() {
		return cantidad_real;
	}

	public void setCantidad_real(Integer cantidad_real) {
		this.cantidad_real = cantidad_real;
	}

	public String getEtapa_actual() {
		return etapa_actual;
	}

	public void setEtapa_actual(String etapa_actual) {
		this.etapa_actual = etapa_actual;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Orden_Produccion [id=" + id_orden_produccion + ", fecha_inicio=" + fecha_inicio + ", etapa="
				+ etapa_actual + ", estimada=" + cantidad_estimada + ", real=" + cantidad_real + "]";
	}
}
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

/**
 * Igual al DER: id, fecha_inicio, fecha_fin (NULL), cantidad_estimada,
 * cantidad_real (NULL), etapa_actual, id_usuario.
 */
@Entity
@Table(name = "orden_produccion")
public class Orden_Produccion {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_orden_produccion;

	@Column(nullable = false)
	private Date fecha_inicio;

	@Column(nullable = true) // se completa cuando la orden se termina de producir
	private Date fecha_fin;

	@Column(nullable = false)
	private int cantidad_estimada;

	@Column(nullable = true) // acumulado producido hasta ahora
	private Integer cantidad_real;

	@Column(nullable = false, length = 255)
	private String etapa_actual; // Corte, Limpieza, Ovillado, Bobinado, Sellado

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

	// ---- Calculados (no se guardan en la base, no cambian el DER) ----

	// Lo producido hasta ahora (si cantidad_real es null, es 0)
	public int getProducida() {
		return cantidad_real == null ? 0 : cantidad_real;
	}

	// Lo que falta producir
	public int getPendiente() {
		return Math.max(0, cantidad_estimada - getProducida());
	}

	// Pendiente / En producción / Completada
	public String getEstado() {
		if (fecha_fin != null) {
			return "Completada";
		}
		return getProducida() > 0 ? "En producción" : "Pendiente";
	}

	@Override
	public String toString() {
		return "Orden_Produccion [id=" + id_orden_produccion + ", fecha_inicio=" + fecha_inicio + ", etapa="
				+ etapa_actual + ", estimada=" + cantidad_estimada + ", real=" + cantidad_real + "]";
	}
}
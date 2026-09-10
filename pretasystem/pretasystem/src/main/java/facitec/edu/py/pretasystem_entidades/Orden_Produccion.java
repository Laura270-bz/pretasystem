package facitec.edu.py.pretasystem_entidades;

import java.sql.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
	@Column(nullable = false)
	private Date fecha_fin;
	@Column(nullable = false, length = 45)
	private String cantiidad_estimada;
	@Column(nullable = false, length = 45)
	private String cantidad_real;

	public Orden_Produccion() {
		// TODO Auto-generated constructor stub
	}

	public Orden_Produccion(int id_orden_produccion, Date fecha_inicio, Date fecha_fin, String cantiidad_estimada,
			String cantidad_real) {
		super();
		this.id_orden_produccion = id_orden_produccion;
		this.fecha_inicio = fecha_inicio;
		this.fecha_fin = fecha_fin;
		this.cantiidad_estimada = cantiidad_estimada;
		this.cantidad_real = cantidad_real;
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

	public String getCantiidad_estimada() {
		return cantiidad_estimada;
	}

	public void setCantiidad_estimada(String cantiidad_estimada) {
		this.cantiidad_estimada = cantiidad_estimada;
	}

	public String getCantidad_real() {
		return cantidad_real;
	}

	public void setCantidad_real(String cantidad_real) {
		this.cantidad_real = cantidad_real;
	}

	@Override
	public String toString() {
		return "Orden_Produccion [id_orden_produccion=" + id_orden_produccion + ", fecha_inicio=" + fecha_inicio
				+ ", fecha_fin=" + fecha_fin + ", cantiidad_estimada=" + cantiidad_estimada + ", cantidad_real="
				+ cantidad_real + "]";
	}

}

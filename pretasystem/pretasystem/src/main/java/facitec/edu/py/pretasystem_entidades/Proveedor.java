package facitec.edu.py.pretasystem_entidades;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedor")
public class Proveedor {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_proveedores;
	@Column(nullable = false, unique = true, length = 255)
	private String documento;
	@Column(nullable = false, length = 255)
	private String nombre;
	@Column(nullable = false, length = 255)
	private String direccion;
	@Column(nullable = false, length = 255)
	private String telefono;
	@Column(nullable = false, length = 255)
	private String email;

	public Proveedor() {
		// TODO Auto-generated constructor stub
	}

	public Proveedor(int id_proveedores, String documento, String nombre, String direccion, String telefono,
			String email) {
		super();
		this.id_proveedores = id_proveedores;
		this.documento = documento;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.email = email;
	}

	public int getId_proveedores() {
		return id_proveedores;
	}

	public void setId_proveedores(int id_proveedores) {
		this.id_proveedores = id_proveedores;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Proveedor [id_proveedores=" + id_proveedores + ", documento=" + documento + ", nombre=" + nombre
				+ ", direccion=" + direccion + ", telefono=" + telefono + ", email=" + email + "]";
	}

}

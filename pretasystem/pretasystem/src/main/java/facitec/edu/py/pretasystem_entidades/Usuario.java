package facitec.edu.py.pretasystem_entidades;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
	@Id
	@GenericGenerator(name = "cli_generator", strategy = "increment")
	@GeneratedValue(generator = "cli_generator")
	private int id_usuario;
	@Column(nullable = false, length = 255)
	private String usuario;
	@Column(nullable = false, length = 255)
	private String correo;
	@Column(nullable = false, unique = true, length = 255)
	private String contraseña;

	public Usuario() {
		// TODO Auto-generated constructor stub
	}

	public Usuario(int id_usuario, String usuario, String correo, String contraseña) {
		super();
		this.id_usuario = id_usuario;
		this.usuario = usuario;
		this.correo = correo;
		this.contraseña = contraseña;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	@Override
	public String toString() {
		return "Usuario [id_usuario=" + id_usuario + ", usuario=" + usuario + ", correo=" + correo + ", contraseña="
				+ contraseña + "]";
	}

}

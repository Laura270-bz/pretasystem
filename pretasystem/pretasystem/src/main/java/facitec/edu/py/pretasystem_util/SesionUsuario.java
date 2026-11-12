package facitec.edu.py.pretasystem_util;

import facitec.edu.py.pretasystem_entidades.Usuario;

/** Guarda el usuario que inicio sesion, para usarlo en todo el sistema. */
public class SesionUsuario {
	private static Usuario usuario;

	// nombre del usuario administrador (poné el que usás vos para entrar)
	public static final String ADMIN = "admin";

	public static Usuario getUsuario() {
		return usuario;
	}

	public static void setUsuario(Usuario u) {
		usuario = u;
	}

	public static void cerrarSesion() {
		usuario = null;
	}

	public static boolean esAdministrador() {
		return usuario != null && ADMIN.equalsIgnoreCase(usuario.getUsuario());
	}
}
package facitec.edu.py.pretasystem_util;

import facitec.edu.py.pretasystem_entidades.Usuario;

/** Guarda el usuario que inicio sesion, para usarlo en todo el sistema. */
public class SesionUsuario {
	private static Usuario usuario;

	public static Usuario getUsuario() {
		return usuario;
	}

	public static void setUsuario(Usuario u) {
		usuario = u;
	}

	public static void cerrarSesion() {
		usuario = null;
	}
}
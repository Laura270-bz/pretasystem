package facitec.edu.py.pretasystem_dao;

import java.util.List;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Usuario;

public class Usuario_Dao extends GenericDao<Usuario> {

	public Usuario_Dao() {
		super(Usuario.class);
	}

	@Override
	public List<Usuario> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			Query<Usuario> q = getSession().createQuery(
					"from Usuario u where lower(u.usuario) like :f order by u.id_usuario desc", Usuario.class);
			q.setParameter("f", "%" + filtro.toLowerCase() + "%");
			List<Usuario> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	// Devuelve el usuario si nombre y clave (ya hasheada) coinciden; si no, null
	public Usuario autenticar(String nombreUsuario, String claveHash) {
		iniciarTransaccion();
		try {
			Query<Usuario> q = getSession().createQuery(
					"from Usuario u where u.usuario = :u and u.contrasena = :c", Usuario.class);
			q.setParameter("u", nombreUsuario);
			q.setParameter("c", claveHash);
			Usuario resultado = q.uniqueResult();
			getSession().getTransaction().commit();
			return resultado;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	public long contarUsuarios() {
		iniciarTransaccion();
		try {
			Long total = getSession().createQuery("select count(u) from Usuario u", Long.class).getSingleResult();
			getSession().getTransaction().commit();
			return total;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}
}
package facitec.edu.py.pretasystem_dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_util.ConecctionHelper;

public abstract class GenericDao<T> {
	protected Class<T> clase;

	public GenericDao(Class<T> clase) {
		this.clase = clase;
	}

	protected Session getSession() {
		return ConecctionHelper.getSessionFactory().getCurrentSession();
	}

	protected void iniciarTransaccion() {
		if (!getSession().getTransaction().isActive()) {
			getSession().beginTransaction();
		}
	}

	// Guarda (inserta) o actualiza la entidad
	public void guardar(T entity) throws Exception {
		iniciarTransaccion();
		try {
			getSession().saveOrUpdate(entity);
			getSession().getTransaction().commit();
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			System.out.println("Generic DAO - Guardar - ERROR");
			throw e;
		}
	}

	// CORREGIDO: la entidad llega "detached" (la sesion anterior ya se cerro),
	// por eso se hace merge antes de remove.
	public void eliminar(T entity) throws Exception {
		iniciarTransaccion();
		try {
			Object aEliminar = getSession().contains(entity) ? entity : getSession().merge(entity);
			getSession().remove(aEliminar);
			getSession().getTransaction().commit();
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			System.out.println("Generic DAO - Eliminar - ERROR");
			throw e;
		}
	}

	public T recuperarPorId(Serializable id) {
		iniciarTransaccion();
		try {
			T result = getSession().get(clase, id);
			getSession().getTransaction().commit();
			return result;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	// CORREGIDO: antes ordenaba por "id", que no existe en tus entidades.
	// Si queres orden, sobrescribilo en el DAO especifico.
	public List<T> recuperarTodo() {
		iniciarTransaccion();
		try {
			Query<T> query = getSession().createQuery("from " + clase.getName(), clase);
			List<T> lista = query.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	public abstract List<T> recuperarPorFiltro(String filtro);
}
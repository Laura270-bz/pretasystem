
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

    // Inicia una transacción si no existe una activa.
    protected void iniciarTransaccion() {
        if (!getSession().getTransaction().isActive()) {
            getSession().beginTransaction();
        }
    }

    // Revierte la transacción solamente si está activa.
    protected void rollbackTransaccion() {
        if (getSession().getTransaction().isActive()) {
            getSession().getTransaction().rollback();
        }
    }

    // Guarda o actualiza una entidad.
    public void guardar(T entity) throws Exception {
        iniciarTransaccion();

        try {
            getSession().saveOrUpdate(entity);
            getSession().getTransaction().commit();

        } catch (Exception e) {
            rollbackTransaccion();
            System.out.println("Generic DAO - Guardar - ERROR");
            throw e;
        }
    }

    // Elimina una entidad.
    public void eliminar(T entity) throws Exception {
        iniciarTransaccion();

        try {
            Object aEliminar = getSession().contains(entity)
                    ? entity
                    : getSession().merge(entity);

            getSession().remove(aEliminar);
            getSession().getTransaction().commit();

        } catch (Exception e) {
            rollbackTransaccion();
            System.out.println("Generic DAO - Eliminar - ERROR");
            throw e;
        }
    }

    // Recupera una entidad por su identificador.
    public T recuperarPorId(Serializable id) {
        iniciarTransaccion();

        try {
            T result = getSession().get(clase, id);
            getSession().getTransaction().commit();
            return result;

        } catch (Exception e) {
            rollbackTransaccion();
            throw e;
        }
    }

    // Recupera todas las entidades.
    public List<T> recuperarTodo() {
        iniciarTransaccion();

        try {
            Query<T> query = getSession().createQuery(
                    "from " + clase.getSimpleName(), clase);

            List<T> lista = query.getResultList();

            getSession().getTransaction().commit();
            return lista;

        } catch (Exception e) {
            rollbackTransaccion();
            throw e;
        }
    }

    public abstract List<T> recuperarPorFiltro(String filtro);
}

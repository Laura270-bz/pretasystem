
package facitec.edu.py.pretasystem_dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Materia_Prima;

public class Materia_Prima_Dao extends GenericDao<Materia_Prima> {

    // Constructor: indica al GenericDao que trabajará con Materia_Prima.
    public Materia_Prima_Dao() {
        super(Materia_Prima.class);
    }

    /**
     * Recupera las materias primas buscando por nombre.
     * Si el filtro está vacío, devuelve todos los registros.
     */
    @Override
    public List<Materia_Prima> recuperarPorFiltro(String filtro) {

        iniciarTransaccion();

        try {
            // Evita errores si el filtro llega como null.
            String texto = filtro == null
                    ? ""
                    : filtro.toLowerCase(Locale.ROOT).trim();

            // Consulta HQL para buscar por nombre.
            String hql = "from Materia_Prima m "
                    + "where lower(m.nombre) like :filtro "
                    + "order by m.id_materia_prima desc";

            Query<Materia_Prima> query = getSession().createQuery(
                    hql, Materia_Prima.class);

            // Aplica el texto de búsqueda.
            query.setParameter("filtro", "%" + texto + "%");

            List<Materia_Prima> lista = query.getResultList();

            // Confirma la transacción.
            getSession().getTransaction().commit();

            return lista;

        } catch (Exception e) {

            // Revierte la transacción si ocurre un error.
            rollbackTransaccion();

            throw e;
        }
    }

    /**
     * Recupera todas las materias primas.
     * Utiliza el filtro vacío para obtener todos los registros.
     */
    @Override
    public List<Materia_Prima> recuperarTodo() {
        return recuperarPorFiltro("");
    }
}

package facitec.edu.py.pretasystem_dao;

import java.util.List;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Materia_Prima;

public class Materia_Prima_Dao extends GenericDao<Materia_Prima> {

	public Materia_Prima_Dao() {
		super(Materia_Prima.class);
	}

	// Con filtro vacio devuelve todos los registros
	@Override
	public List<Materia_Prima> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			String hql = "from Materia_Prima m where lower(m.nombre) like :filtro order by m.id_materia_prima desc";
			// CORREGIDO: createQuery (createNamedQuery es solo para queries con nombre)
			Query<Materia_Prima> query = getSession().createQuery(hql, Materia_Prima.class);
			query.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
			List<Materia_Prima> lista = query.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public List<Materia_Prima> recuperarTodo() {
		return recuperarPorFiltro("");
	}
}
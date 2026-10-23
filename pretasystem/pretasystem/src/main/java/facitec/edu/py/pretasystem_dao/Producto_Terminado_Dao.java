package facitec.edu.py.pretasystem_dao;

import java.util.List;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Producto_Terminado;

public class Producto_Terminado_Dao extends GenericDao<Producto_Terminado> {

	public Producto_Terminado_Dao() {
		super(Producto_Terminado.class);
	}

	// Busca por nombre; con filtro vacio devuelve todos
	@Override
	public List<Producto_Terminado> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			Query<Producto_Terminado> q = getSession().createQuery(
					"from Producto_Terminado p where lower(p.nombre) like :f order by p.id_producto_terminado desc",
					Producto_Terminado.class);
			q.setParameter("f", "%" + filtro.toLowerCase() + "%");
			List<Producto_Terminado> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}
}
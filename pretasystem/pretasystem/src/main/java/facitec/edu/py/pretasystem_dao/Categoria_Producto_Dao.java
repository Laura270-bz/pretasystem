package facitec.edu.py.pretasystem_dao;

import java.util.List;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Categoria_Producto;

public class Categoria_Producto_Dao extends GenericDao<Categoria_Producto> {

	public Categoria_Producto_Dao() {
		super(Categoria_Producto.class);
	}

	@Override
	public List<Categoria_Producto> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			Query<Categoria_Producto> q = getSession().createQuery(
					"from Categoria_Producto c where lower(c.nombre_categoria) like :f order by c.id_categoria_producto desc",
					Categoria_Producto.class);
			q.setParameter("f", "%" + filtro.toLowerCase() + "%");
			List<Categoria_Producto> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}
}
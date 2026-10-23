package facitec.edu.py.pretasystem_dao;

import java.util.List;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Proveedor;

public class Proveedor_Dao extends GenericDao<Proveedor> {

	public Proveedor_Dao() {
		super(Proveedor.class);
	}

	// Busca por nombre o documento
	@Override
	public List<Proveedor> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			Query<Proveedor> q = getSession().createQuery(
					"from Proveedor p where lower(p.nombre) like :f or lower(p.documento) like :f order by p.id_proveedores desc",
					Proveedor.class);
			q.setParameter("f", "%" + filtro.toLowerCase() + "%");
			List<Proveedor> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}
}
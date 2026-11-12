
package facitec.edu.py.pretasystem_dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Proveedor;

public class Proveedor_Dao extends GenericDao<Proveedor> {

	public Proveedor_Dao() {
		super(Proveedor.class);
	}

	/**
	 * Comprueba si ya existe un proveedor con el mismo documento.
	 *
	 * @param documento documento que se desea verificar.
	 * @param idExcluir ID del proveedor que se está editando. Si es null, se
	 *                  verifica sin excluir registros.
	 */
	public boolean existeDocumento(String documento, Integer idExcluir) {

		String doc = documento == null ? "" : documento.trim().toLowerCase(Locale.ROOT);

		iniciarTransaccion();

		try {

			String hql = "select count(p.id_proveedores) " + "from Proveedor p "
					+ "where lower(trim(p.documento)) = :doc";

			if (idExcluir != null) {
				hql += " and p.id_proveedores <> :id";
			}

			Query<Long> q = getSession().createQuery(hql, Long.class);
			q.setParameter("doc", doc);

			if (idExcluir != null) {
				q.setParameter("id", idExcluir);
			}

			Long cantidad = q.uniqueResult();

			getSession().getTransaction().commit();

			return cantidad != null && cantidad > 0;

		} catch (Exception e) {

			rollbackTransaccion();
			throw e;
		}
	}

	/**
	 * Recupera proveedores por nombre o documento.
	 */
	@Override
	public List<Proveedor> recuperarPorFiltro(String filtro) {

		String texto = filtro == null ? "" : filtro.trim().toLowerCase(Locale.ROOT);

		iniciarTransaccion();

		try {

			String hql = "from Proveedor p " + "where lower(p.nombre) like :f " + "or lower(p.documento) like :f "
					+ "order by p.id_proveedores desc";

			Query<Proveedor> q = getSession().createQuery(hql, Proveedor.class);
			q.setParameter("f", "%" + texto + "%");

			List<Proveedor> lista = q.getResultList();

			getSession().getTransaction().commit();

			return lista;

		} catch (Exception e) {

			rollbackTransaccion();
			throw e;
		}
	}

	/**
	 * Recupera todos los proveedores para el reporte.
	 */
	public List<Proveedor> listarParaReporteProveedor() {

		iniciarTransaccion();

		try {

			String hql = "from Proveedor p " + "order by p.id_proveedores desc";

			Query<Proveedor> q = getSession().createQuery(hql, Proveedor.class);

			List<Proveedor> lista = q.getResultList();

			getSession().getTransaction().commit();

			return lista;

		} catch (Exception e) {

			rollbackTransaccion();
			throw e;
		}
	}
}

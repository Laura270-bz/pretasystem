
package facitec.edu.py.pretasystem_dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;

public class Consumo_Materia_Prima_Dao extends GenericDao<Consumo_Materia_Prima> {

	public Consumo_Materia_Prima_Dao() {
		super(Consumo_Materia_Prima.class);
	}

	/**
	 * Recupera los consumos filtrando por nombre de materia prima o etapa actual de
	 * la orden de produccion.
	 */
	@Override
	public List<Consumo_Materia_Prima> recuperarPorFiltro(String filtro) {

		iniciarTransaccion();

		try {

			String texto = filtro == null ? "" : filtro.trim().toLowerCase(Locale.ROOT);

			String hql = "select distinct c from Consumo_Materia_Prima c " + "left join fetch c.materia_prima m "
					+ "left join fetch c.orden_produccion o " + "where lower(m.nombre) like :f "
					+ "or lower(o.etapa_actual) like :f " + "order by c.id_consumo_materia_prima desc";

			Query<Consumo_Materia_Prima> query = getSession().createQuery(hql, Consumo_Materia_Prima.class);

			query.setParameter("f", "%" + texto + "%");

			List<Consumo_Materia_Prima> lista = query.getResultList();

			getSession().getTransaction().commit();

			return lista;

		} catch (Exception e) {

			if (getSession().getTransaction().isActive()) {
				getSession().getTransaction().rollback();
			}

			System.out.println("Consumo Materia Prima DAO - ERROR");
			throw e;
		}
	}

	/**
	 * Recupera todos los consumos para generar el reporte. Incluye la materia prima
	 * y la orden de produccion asociadas.
	 */
	public List<Consumo_Materia_Prima> listarParaReporteConsumoMateriaPrima() {

		iniciarTransaccion();

		try {

			String hql = "select c from Consumo_Materia_Prima c " + "join fetch c.materia_prima "
					+ "join fetch c.orden_produccion " + "order by c.id_consumo_materia_prima desc";

			Query<Consumo_Materia_Prima> query = getSession().createQuery(hql, Consumo_Materia_Prima.class);

			List<Consumo_Materia_Prima> lista = query.getResultList();

			getSession().getTransaction().commit();

			return lista;

		} catch (Exception e) {

			if (getSession().getTransaction().isActive()) {
				getSession().getTransaction().rollback();
			}

			System.out.println("Consumo Materia Prima DAO - listarParaReporte - ERROR");
			throw e;
		}
	}
}

package facitec.edu.py.pretasystem_dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Consumo_Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Materia_Prima;
import facitec.edu.py.pretasystem_entidades.Orden_Produccion;

public class Orden_Produccion_Dao extends GenericDao<Orden_Produccion> {

	public Orden_Produccion_Dao() {
		super(Orden_Produccion.class);
	}

	/**
	 * TRANSACCION COMPLETA: Orden de Produccion + Consumo de Materia Prima.
	 * En UNA sola transaccion:
	 *  1) valida que haya stock suficiente de cada materia prima
	 *  2) descuenta el stock
	 *  3) guarda la orden de produccion
	 *  4) guarda cada consumo (detalle)
	 * Si algo falla, no se guarda NADA (rollback).
	 *
	 * @return lista de alertas (materias primas que quedaron en o bajo el stock minimo)
	 */
	public List<String> guardarOrdenConConsumos(Orden_Produccion orden, List<Consumo_Materia_Prima> consumos)
			throws Exception {
		List<String> alertas = new ArrayList<>();
		iniciarTransaccion();
		Session s = getSession();
		try {
			for (Consumo_Materia_Prima c : consumos) {
				// se vuelve a leer de la base para tener el stock real y actual
				Materia_Prima mp = s.get(Materia_Prima.class, c.getMateria_prima().getId_materia_prima());

				if (c.getCantidad_usada() <= 0) {
					throw new Exception("La cantidad de " + mp.getNombre() + " debe ser mayor a 0");
				}
				if (mp.getStock_actual() < c.getCantidad_usada()) {
					throw new Exception("Stock insuficiente de " + mp.getNombre() + ". Disponible: "
							+ mp.getStock_actual() + " " + mp.getUnidad_medida());
				}

				mp.setStock_actual(mp.getStock_actual() - c.getCantidad_usada()); // descuento
				c.setMateria_prima(mp);

				if (mp.getStock_actual() <= mp.getStock_minimo()) {
					alertas.add(mp.getNombre() + " quedo con " + mp.getStock_actual() + " (minimo "
							+ mp.getStock_minimo() + ")");
				}
			}

			s.persist(orden); // obtiene su id (generador increment)

			for (Consumo_Materia_Prima c : consumos) {
				c.setOrden_produccion(orden);
				s.persist(c);
			}

			s.getTransaction().commit();
			return alertas;
		} catch (Exception e) {
			s.getTransaction().rollback();
			System.out.println("Orden_Produccion_Dao - guardarOrdenConConsumos - ERROR");
			throw e;
		}
	}

	// Consumos de una orden (para mostrarlos al abrir una orden existente)
	public List<Consumo_Materia_Prima> recuperarConsumos(int idOrden) {
		iniciarTransaccion();
		try {
			Query<Consumo_Materia_Prima> q = getSession().createQuery(
					"from Consumo_Materia_Prima c where c.orden_produccion.id_orden_produccion = :id",
					Consumo_Materia_Prima.class);
			q.setParameter("id", idOrden);
			List<Consumo_Materia_Prima> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public List<Orden_Produccion> recuperarPorFiltro(String filtro) {
		iniciarTransaccion();
		try {
			Query<Orden_Produccion> q = getSession().createQuery(
					"from Orden_Produccion o where lower(o.etapa_actual) like :f order by o.id_orden_produccion desc",
					Orden_Produccion.class);
			q.setParameter("f", "%" + filtro.toLowerCase() + "%");
			List<Orden_Produccion> lista = q.getResultList();
			getSession().getTransaction().commit();
			return lista;
		} catch (Exception e) {
			getSession().getTransaction().rollback();
			throw e;
		}
	}
}
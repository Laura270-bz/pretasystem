
package facitec.edu.py.pretasystem_dao;

import java.sql.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.query.Query;

import facitec.edu.py.pretasystem_entidades.Orden_Produccion;
import facitec.edu.py.pretasystem_entidades.Producto_Terminado;

public class Producto_Terminado_Dao extends GenericDao<Producto_Terminado> {

	public Producto_Terminado_Dao() {
		super(Producto_Terminado.class);
	}

	/**
	 * Busca productos por nombre, color o nombre de categoria. Si el filtro esta
	 * vacio, recupera todos los productos.
	 */
	@Override
	public List<Producto_Terminado> recuperarPorFiltro(String filtro) {

		iniciarTransaccion();

		try {

			String texto = filtro == null ? "" : filtro.trim().toLowerCase(Locale.ROOT);

			Query<Producto_Terminado> q = getSession().createQuery("select distinct p from Producto_Terminado p "
					+ "left join fetch p.categoria_producto c " + "left join fetch p.orden_produccion o "
					+ "where lower(p.nombre) like :f " + "or lower(p.color) like :f "
					+ "or lower(c.nombre_categoria) like :f " + "order by p.id_producto_terminado desc",
					Producto_Terminado.class);

			q.setParameter("f", "%" + texto + "%");

			List<Producto_Terminado> lista = q.getResultList();

			getSession().getTransaction().commit();

			return lista;

		} catch (Exception e) {

			rollbackTransaccion();
			throw e;
		}
	}

	/**
	 * Guarda o actualiza un producto terminado.
	 *
	 * Producto nuevo: Stock final = stock inicial + cantidad producida.
	 *
	 * Producto existente: Recupera el stock actual de la base de datos y suma
	 * solamente la nueva cantidad producida.
	 *
	 * La actualizacion del producto y de la orden se realiza dentro de una misma
	 * transaccion.
	 */
	public void guardarConProduccion(Producto_Terminado p, Orden_Produccion ordenSel, int producido, Date fecha)
			throws Exception {

		if (p == null) {
			throw new Exception("El producto no puede ser nulo");
		}

		if (producido < 0) {
			throw new Exception("La cantidad producida no puede ser negativa");
		}

		if (producido > 0 && ordenSel == null) {
			throw new Exception("Debe seleccionar una orden de produccion");
		}

		if (producido > 0 && fecha == null) {
			throw new Exception("Debe indicar la fecha de produccion");
		}

		int stockOriginal = p.getStock_actual();
		Orden_Produccion ordenOriginal = p.getOrden_produccion();

		boolean nuevo = p.getId_producto_terminado() <= 0;

		iniciarTransaccion();

		Session s = getSession();

		try {

			Producto_Terminado productoGuardar;

			if (nuevo) {

				// Producto nuevo: se utiliza el stock inicial ingresado.
				productoGuardar = p;

			} else {

				// Recupera el producto desde la base de datos.
				productoGuardar = s.get(Producto_Terminado.class, p.getId_producto_terminado());

				if (productoGuardar == null) {
					throw new Exception("El producto que desea actualizar no existe");
				}

				// Actualiza los datos generales del producto.
				productoGuardar.setNombre(p.getNombre());
				productoGuardar.setColor(p.getColor());
				productoGuardar.setCategoria_producto(p.getCategoria_producto());
				productoGuardar.setStock_minimo(p.getStock_minimo());
				productoGuardar.setPrecio_minorista(p.getPrecio_minorista());
				productoGuardar.setPrecio_mayorista(p.getPrecio_mayorista());

				// Se conserva el stock recuperado de la base de datos.
			}

			if (ordenSel != null) {

				// Recupera la orden desde la base de datos.
				Orden_Produccion o = s.get(Orden_Produccion.class, ordenSel.getId_orden_produccion());

				if (o == null) {
					throw new Exception("La orden seleccionada no existe");
				}

				if (producido > 0) {

					// Verifica que la orden tenga una fecha de inicio.
					if (o.getFecha_inicio() == null) {
						throw new Exception("La orden seleccionada no tiene una fecha de inicio registrada");
					}

					// Verifica que la fecha de produccion no sea anterior al inicio de la orden.
					if (fecha.before(o.getFecha_inicio())) {
						throw new Exception(
								"La fecha de produccion no puede ser anterior a la fecha de inicio de la orden.\n"
										+ "Fecha de inicio: " + o.getFecha_inicio() + "\nFecha de produccion: "
										+ fecha);
					}

					// Verifica que la orden no este finalizada.
					if (o.getFecha_fin() != null) {
						throw new Exception("La orden ya esta completada");
					}

					// Verifica que no se supere la cantidad pendiente.
					if (producido > o.getPendiente()) {
						throw new Exception("Lo producido supera lo pendiente de la orden (" + o.getPendiente() + ")");
					}

					/*
					 * SUMA AUTOMATICA DEL STOCK.
					 *
					 * En productos nuevos utiliza el stock inicial. En productos existentes utiliza
					 * el stock recuperado de la base de datos.
					 */
					int nuevoStock = productoGuardar.getStock_actual() + producido;

					productoGuardar.setStock_actual(nuevoStock);

					// Actualiza la cantidad realmente producida.
					int nuevaReal = o.getProducida() + producido;

					o.setCantidad_real(nuevaReal);

					// Registra la fecha de finalizacion si se completo la orden.
					if (nuevaReal >= o.getCantidad_estimada()) {
						o.setFecha_fin(fecha);
					}
				}

				// Relaciona el producto con la orden seleccionada.
				productoGuardar.setOrden_produccion(o);
			}

			/*
			 * Si es nuevo, se inserta. Si ya existe, Hibernate detecta los cambios en la
			 * entidad administrada.
			 */
			if (nuevo) {
				s.persist(productoGuardar);
			}

			// Confirma los cambios.
			s.getTransaction().commit();

		} catch (Exception e) {

			// Revierte los cambios si ocurre algun error.
			rollbackTransaccion();

			// Restaura los valores originales del objeto recibido.
			p.setStock_actual(stockOriginal);
			p.setOrden_produccion(ordenOriginal);

			System.out.println("Producto_Terminado_Dao - guardarConProduccion - ERROR");

			throw e;
		}
	}
}

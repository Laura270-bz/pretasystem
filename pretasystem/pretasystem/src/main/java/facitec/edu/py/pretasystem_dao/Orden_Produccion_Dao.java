
package facitec.edu.py.pretasystem_dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
     * Registra una orden de producción junto con sus consumos de materia prima.
     */
    public List<String> guardarOrdenConConsumos(
            Orden_Produccion orden,
            List<Consumo_Materia_Prima> consumos) throws Exception {

        if (orden == null) {
            throw new Exception("La orden de producción no puede ser nula.");
        }

        if (consumos == null) {
            throw new Exception("La lista de consumos no puede ser nula.");
        }

        List<String> alertas = new ArrayList<>();

        iniciarTransaccion();
        Session s = getSession();

        try {

            for (Consumo_Materia_Prima c : consumos) {

                if (c == null) {
                    throw new Exception("Existe un consumo vacío en la lista.");
                }

                if (c.getMateria_prima() == null) {
                    throw new Exception("Debe seleccionar una materia prima.");
                }

                if (c.getCantidad_usada() <= 0) {
                    throw new Exception("La cantidad utilizada debe ser mayor a 0.");
                }

                int idMateriaPrima = c.getMateria_prima().getId_materia_prima();

                Materia_Prima mp = s.get(Materia_Prima.class, idMateriaPrima);

                if (mp == null) {
                    throw new Exception(
                            "No se encontró la materia prima con ID: " + idMateriaPrima
                    );
                }

                if (mp.getStock_actual() < c.getCantidad_usada()) {
                    throw new Exception(
                            "Stock insuficiente de " + mp.getNombre()
                            + ". Disponible: " + mp.getStock_actual()
                            + " " + mp.getUnidad_medida()
                    );
                }

                mp.setStock_actual(
                        mp.getStock_actual() - c.getCantidad_usada()
                );

                c.setMateria_prima(mp);

                if (mp.getStock_actual() <= mp.getStock_minimo()) {
                    alertas.add(
                            mp.getNombre()
                            + " quedó con " + mp.getStock_actual()
                            + " (mínimo " + mp.getStock_minimo() + ")"
                    );
                }
            }

            s.persist(orden);

            for (Consumo_Materia_Prima c : consumos) {
                c.setOrden_produccion(orden);
                s.persist(c);
            }

            s.getTransaction().commit();

            return alertas;

        } catch (Exception e) {

            rollbackTransaccion();

            System.out.println(
                    "Orden_Produccion_Dao - guardarOrdenConConsumos - ERROR"
            );

            throw e;
        }
    }

    /**
     * Recupera todas las órdenes de producción para el reporte.
     * Incluye órdenes pendientes, en producción y completadas.
     */
    public List<Orden_Produccion> listarParaReporteOrdenProduccion() {

        iniciarTransaccion();

        try {

            Query<Orden_Produccion> q = getSession().createQuery(
                    "select o from Orden_Produccion o "
                    + "left join fetch o.usuario "
                    + "order by o.id_orden_produccion desc",
                    Orden_Produccion.class
            );

            List<Orden_Produccion> lista = q.getResultList();

            getSession().getTransaction().commit();

            return lista;

        } catch (Exception e) {

            rollbackTransaccion();

            e.printStackTrace();

            throw e;
        }
    }

    /**
     * Recupera las órdenes que todavía no tienen fecha de finalización.
     */
    public List<Orden_Produccion> recuperarPendientes() {

        iniciarTransaccion();

        try {

            Query<Orden_Produccion> q = getSession().createQuery(
                    "from Orden_Produccion o "
                    + "where o.fecha_fin is null "
                    + "order by o.id_orden_produccion desc",
                    Orden_Produccion.class
            );

            List<Orden_Produccion> lista = q.getResultList();

            getSession().getTransaction().commit();

            return lista;

        } catch (Exception e) {

            rollbackTransaccion();

            throw e;
        }
    }

    /**
     * Recupera los consumos de materia prima de una orden específica.
     */
    public List<Consumo_Materia_Prima> recuperarConsumos(int idOrden) {

        iniciarTransaccion();

        try {

            Query<Consumo_Materia_Prima> q = getSession().createQuery(
                    "from Consumo_Materia_Prima c "
                    + "where c.orden_produccion.id_orden_produccion = :id",
                    Consumo_Materia_Prima.class
            );

            q.setParameter("id", idOrden);

            List<Consumo_Materia_Prima> lista = q.getResultList();

            getSession().getTransaction().commit();

            return lista;

        } catch (Exception e) {

            rollbackTransaccion();

            throw e;
        }
    }

    /**
     * Recupera órdenes de producción según la etapa actual.
     */
    @Override
    public List<Orden_Produccion> recuperarPorFiltro(String filtro) {

        iniciarTransaccion();

        try {

            String texto = (filtro == null)
                    ? ""
                    : filtro.trim().toLowerCase(Locale.ROOT);

            Query<Orden_Produccion> q = getSession().createQuery(
                    "from Orden_Produccion o "
                    + "where lower(o.etapa_actual) like :f "
                    + "order by o.id_orden_produccion desc",
                    Orden_Produccion.class
            );

            q.setParameter("f", "%" + texto + "%");

            List<Orden_Produccion> lista = q.getResultList();

            getSession().getTransaction().commit();

            return lista;

        } catch (Exception e) {

            rollbackTransaccion();

            throw e;
        }
    }
}

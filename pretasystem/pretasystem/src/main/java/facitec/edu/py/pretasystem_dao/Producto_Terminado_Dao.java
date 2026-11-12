
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
     * Busca productos por nombre, color o nombre de categoría.
     */
    @Override
    public List<Producto_Terminado> recuperarPorFiltro(String filtro) {

        iniciarTransaccion();

        try {

            String texto = filtro == null
                    ? ""
                    : filtro.trim().toLowerCase(Locale.ROOT);

            Query<Producto_Terminado> q = getSession().createQuery(
                    "select distinct p from Producto_Terminado p "
                    + "left join fetch p.categoria_producto c "
                    + "left join fetch p.orden_produccion o "
                    + "where lower(p.nombre) like :f "
                    + "or lower(p.color) like :f "
                    + "or lower(c.nombre_categoria) like :f "
                    + "order by p.id_producto_terminado desc",
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
     * Guarda o actualiza un producto terminado y registra la producción.
     *
     * La cantidad producida se acumula en la orden.
     * El stock se incrementa únicamente con la nueva tanda producida.
     */
    public void guardarConProduccion(
            Producto_Terminado p,
            Orden_Produccion ordenSel,
            int producido,
            Date fecha) throws Exception {

        if (p == null) {
            throw new Exception("El producto no puede ser nulo");
        }

        if (producido < 0) {
            throw new Exception("La cantidad producida no puede ser negativa");
        }

        if (producido > 0 && ordenSel == null) {
            throw new Exception("Debe seleccionar una orden de producción");
        }

        if (producido > 0 && fecha == null) {
            throw new Exception("Debe indicar la fecha de producción");
        }

        int stockOriginal = p.getStock_actual();
        Orden_Produccion ordenOriginal = p.getOrden_produccion();

        boolean nuevo = p.getId_producto_terminado() <= 0;

        iniciarTransaccion();

        Session s = getSession();

        try {

            Producto_Terminado productoGuardar;

            if (nuevo) {

                productoGuardar = p;

                /*
                 * El stock inicial ingresado en el formulario no debe
                 * sumarse nuevamente a cada tanda de producción.
                 *
                 * En este cálculo se parte de cero y se agregan
                 * únicamente las unidades realmente producidas.
                 */
                productoGuardar.setStock_actual(0);

            } else {

                productoGuardar = s.get(
                        Producto_Terminado.class,
                        p.getId_producto_terminado());

                if (productoGuardar == null) {
                    throw new Exception(
                            "El producto que desea actualizar no existe");
                }

                productoGuardar.setNombre(p.getNombre());
                productoGuardar.setColor(p.getColor());
                productoGuardar.setCategoria_producto(
                        p.getCategoria_producto());
                productoGuardar.setStock_minimo(p.getStock_minimo());
                productoGuardar.setPrecio_minorista(
                        p.getPrecio_minorista());
                productoGuardar.setPrecio_mayorista(
                        p.getPrecio_mayorista());

                // Se conserva el stock actual recuperado de la base de datos.
            }

            if (ordenSel != null) {

                Orden_Produccion o = s.get(
                        Orden_Produccion.class,
                        ordenSel.getId_orden_produccion());

                if (o == null) {
                    throw new Exception(
                            "La orden seleccionada no existe");
                }

                if (producido > 0) {

                    if (o.getFecha_inicio() == null) {
                        throw new Exception(
                                "La orden seleccionada no tiene una fecha de inicio registrada");
                    }

                    if (fecha.before(o.getFecha_inicio())) {
                        throw new Exception(
                                "La fecha de producción no puede ser anterior "
                                + "a la fecha de inicio de la orden.\n"
                                + "Fecha de inicio: " + o.getFecha_inicio()
                                + "\nFecha de producción: " + fecha);
                    }

                    if (o.getFecha_fin() != null) {
                        throw new Exception(
                                "La orden ya está completada");
                    }

                    if (producido > o.getPendiente()) {
                        throw new Exception(
                                "Lo producido supera lo pendiente de la orden ("
                                + o.getPendiente() + ")");
                    }

                    /*
                     * STOCK:
                     * Se suma solamente la nueva tanda producida.
                     * No se vuelve a sumar el stock inicial.
                     */
                    int nuevoStock =
                            productoGuardar.getStock_actual() + producido;

                    productoGuardar.setStock_actual(nuevoStock);

                    /*
                     * PRODUCCIÓN:
                     * Se acumula la cantidad producida en la orden.
                     */
                    int nuevaReal = o.getProducida() + producido;

                    o.setCantidad_real(nuevaReal);

                    if (nuevaReal >= o.getCantidad_estimada()) {
                        o.setFecha_fin(fecha);
                    }
                }

                productoGuardar.setOrden_produccion(o);
            }

            if (nuevo) {
                s.persist(productoGuardar);
            }

            s.getTransaction().commit();

        } catch (Exception e) {

            rollbackTransaccion();

            p.setStock_actual(stockOriginal);
            p.setOrden_produccion(ordenOriginal);

            System.out.println(
                    "Producto_Terminado_Dao - guardarConProduccion - ERROR");

            throw e;
        }
    }
}

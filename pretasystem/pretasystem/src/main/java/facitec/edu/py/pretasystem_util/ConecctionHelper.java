
package facitec.edu.py.pretasystem_util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class ConecctionHelper {

    private static volatile SessionFactory sessionFactory;

    // Obtiene la SessionFactory o la inicializa si todavía no existe.
    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            synchronized (ConecctionHelper.class) {
                if (sessionFactory == null) {
                    setUp();
                }
            }
        }

        return sessionFactory;
    }

    // Inicializa Hibernate.
    private static void setUp() {

        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder().configure().build();

        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

        } catch (Exception e) {

            StandardServiceRegistryBuilder.destroy(registry);

            throw new ExceptionInInitializerError(e);
        }
    }

    // Cierra Hibernate cuando termina el sistema.
    public static void cerrarSessionFactory() {

        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    public static void main(String[] args) {

        System.out.println(
                ConecctionHelper.getSessionFactory()
                + "################################"
        );

        cerrarSessionFactory();
    }
}

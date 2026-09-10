package facitec.edu.py.pretasystem_vista;
//Se encarga de configurar y mantener una unica conexion global 

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class HibernateUtil {
	public static SessionFactory SessionFactory;
	public static SessionFactory getSessionFactory() {
		if (SessionFactory == null)
			setUp();
		return SessionFactory;
	}

	public static void setUp() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

		try {
			SessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			StandardServiceRegistryBuilder.destroy(registry);
			e.printStackTrace();
		}

	}
	
	public static void main (String[] args) {
		System.out.println(HibernateUtil.getSessionFactory() + "Exito!!########################################################");
	}

}

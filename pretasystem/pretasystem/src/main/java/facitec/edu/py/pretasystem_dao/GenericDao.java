package facitec.edu.py.pretasystem_dao;

import org.hibernate.Session;

import facitec.edu.py.pretasystem_util.ConecctionHelper;



public abstract class GenericDao<T> {
	protected Class<T> clase;

	public GenericDao(Class<T> clase) {
		this.clase = clase;
	}

	protected Session getSession() {
		return ConecctionHelper.getSessionFactory().getCurrentSession();

	}

	protected void iniciarTransaccion() {
		if (!getSession().getTransaction().isActive()) {
			getSession().beginTransaction();
		}
		}
		// significa que va recibir cualquier tipo de entidad y es
		// esa entidad lo que vamos a persistir por lo tanto se pasaa .persist(entity)
		public void guardar(T entity) throws Exception {
			iniciarTransaccion();
			try {
				getSession().saveOrUpdate(entity);
				getSession().getTransaction().commit();// si no ahi error hace que se confirme los datos en la base de datos
			} catch (Exception e) {
				getSession().getTransaction().rollback();// lo que hace es cancelar la ejecucion de la base de datos
				System.out.println("Generic DAO - Guardar - ERROR");
				throw e;
			}

		}

}

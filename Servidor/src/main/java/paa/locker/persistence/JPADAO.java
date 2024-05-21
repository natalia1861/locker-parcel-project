package paa.locker.persistence;

import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JPADAO<T, K> implements DAO<T, K> {
    protected EntityManager em;
    protected Class<T> clazz;

    public JPADAO(EntityManager em, Class<T> entityClass) {
        this.clazz = entityClass;
        this.em = em;
    }

    @Override
    public T find(K id) {
    	T t = em.find(clazz, id);
    	
    	//si no se encuentra debe devolver null (especificado)
    	return t;
    }

    @Override
    public T create(T t) {
        try {
        	em.persist(t);
        	em.flush();
        	em.refresh(t);
        	return t;
        } catch (EntityExistsException ex) {
        	throw new DAOException ("El objeto ya existe");
        }
    }

    @Override
    public T update(T t) {
    	try {
    		return em.merge(t);
    	} catch (IllegalArgumentException ex) {
    		throw new DAOException("La entidad no existe");
    	}
    }

    @Override
    public void delete(T t) {
    	try {
            em.remove(t);
    	} catch (IllegalArgumentException ex) {
    		throw new DAOException("La entidad no existe");
    	}
    }

    @Override
    public List<T> findAll() {
    	
    	try {
    		
    		java.lang.String TABLE = clazz.getName();
    		Query q = em.createQuery("select t from " + TABLE + " t"); 
    		List<T> list = q.getResultList();  //warning cast
    		return list;
    	
    	} catch (IllegalArgumentException e) {
    		throw new DAOException("La lista no pudo ser creada");
    	}
        // Complete este método, que debe listar todos los objetos de la clase T
        // Necesitará hacer consultas a la base datos mediante una TypedQuery, bien
        // empleando una sentencia JPQL o una CriteriaQuery
    }
}

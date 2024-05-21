package paa.locker.persistence;

import javax.persistence.*;

import paa.locker.model.Locker;

public class LockerJPADAO extends JPADAO <Locker, Long>{
	private final static Class<Locker> clazz = Locker.class;
	
	public LockerJPADAO(EntityManager em) {
		super(em, clazz);
	}
	

}

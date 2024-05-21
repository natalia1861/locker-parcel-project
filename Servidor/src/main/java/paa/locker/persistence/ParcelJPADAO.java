package paa.locker.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import paa.locker.model.Parcel;

public class ParcelJPADAO extends JPADAO<Parcel, Long>{
	private final static Class<Parcel> clazz = Parcel.class;
	
	public ParcelJPADAO(EntityManager em) {
		super(em, clazz);
	}

	//Devuelve una lista de los Parcels limitados por un peso superior y otro inferior
	//estas variables seran para distinguir lo que es un paquete grande y otro pequeño

	//si esta vacia implica que no hay ningun paquete que cumpla dichas condiciones de peso
	
	public List<Parcel> limitarPorPeso(List<Parcel> lista, float lowWeight, float highWeight) {
		List<Parcel> l = new ArrayList<Parcel> ();
		for (Parcel x: lista) {
			if (x.getWeight() <= highWeight && x.getWeight() >= lowWeight) {
				l.add(x);
			}
		}
			return l;
	}
	
	//Devuelve una lista de los Parcels limitados por una fecha superior y otra inferior
	//En principio se le pasara la fecha de la que se busca una llegada y otra que es el maximo tiempo que esté
	
	//si esta vacia implica que no hay ningun paquete con dihcas condiciones de fecha
	
	public List<Parcel> limitarPorFecha(List<Parcel> lista, LocalDate lowDate, LocalDate hihgDate) {
		List<Parcel> l = new ArrayList<Parcel> ();
		for (Parcel x: lista) {
			if ((x.getArrivalDate().isAfter(lowDate) || x.getArrivalDate().isEqual(lowDate)) && (x.getArrivalDate().isBefore(hihgDate) || x.getArrivalDate().isEqual(hihgDate))) {
				l.add(x);
			}
		}
		
		return l;

	}
	
	//Devuelve todos los Parcels que tenga un usuario (DNI)
	
	//si esta vacia implica que el usuario es nuevo (no deberia dar error)
	
	public List<Parcel> limitarPorUsuario (List<Parcel> lista, int DNI) {
		List<Parcel> l = new ArrayList<Parcel> ();
		for (Parcel x:  lista) {
			if (x.getAddressee() == DNI) {
				l.add(x);
			}
		}
		
		return l;
	}
	
	//Devuelve una lista de los Parcels que se encuentren en un armario determinado
	
	//si esta vacia implica que no hay paquetes en dicho armario (no da error)
	
	public List<Parcel> limitarPorLocker(List<Parcel> lista, long lockerCode) {
		List<Parcel> l = new ArrayList<Parcel> ();
		for(Parcel x: lista) {
			if (x.getLocker().getCode().equals(lockerCode)) {
				l.add(x);
			}
		}
		
		return l;
	} 

}
 package paa.locker.business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import paa.locker.model.Locker;
import paa.locker.model.Parcel;
import paa.locker.persistence.DAOException;
import paa.locker.persistence.LockerJPADAO;
import paa.locker.persistence.ParcelJPADAO;

public class JPAParcelService implements ParcelService {
	private final String PERSISTENCE_UNIT_NAME = "paa";
	private EntityManagerFactory emf;
	
	public JPAParcelService(String path) {
		 Map<String, String> properties = new HashMap<String, String>();
		 properties.put("javax.persistence.jdbc.url", "jdbc:derby:"+path+";create=true");
		 emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
	}
	
	@Override
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,
			int smallCompartments) {

		Locker t = initializeLockerValues(name, address, longitude, latitude, largeCompartments, smallCompartments);
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		try {
			tx.begin();
			LockerJPADAO lockerdao = new LockerJPADAO(em);
			
			lockerdao.create(t);
			
			em.getTransaction().commit();
		} catch (DAOException | ParcelServiceException e) {
			try {
			em.getTransaction().rollback();
			throw new ParcelServiceException(e.getMessage());
			
			} catch (Exception ex) {
				ex.printStackTrace(); //error fuera de lo normal
			}
			
		} finally {
			em.close();
		}
		return t;
	}

	@Override
	public Locker findLocker(Long lockerCode) {
		
		EntityManager em = emf.createEntityManager();
		
		Locker t = null;
		
		try {
			LockerJPADAO lockerdao = new LockerJPADAO(em);
			
			t = lockerdao.find(lockerCode);
			
		} catch (DAOException e) {
			//NO DEBERIA DEVOLVER NINGUNA EXCEPCION (dead code)
			throw new ParcelServiceException(e.getMessage());
		} finally {
			em.close();
		}
		return t;
	}

	@Override
	public List<Locker> findAllLockers() {
		
		EntityManager em = emf.createEntityManager();
		List<Locker> lista = null;
		
		try {
			LockerJPADAO lockerdao = new LockerJPADAO(em);
			lista = lockerdao.findAll();
			
		} catch (DAOException e) {
			//NO DEBERIA DEVOLVER NINGUNA EXCEPCION (a no ser que haya programado mal el metodo)
			throw new ParcelServiceException(e.getMessage());
		} finally {
			em.close();
		}
		return lista;
	}

	/* AviableCompartments
	 * Método que permite mirar los huecos disponibles para introducir un nuevo paquete en un armario
	 * 		Paquete con Peso Adecuado, peso + y < largeMaxWeight
	 * 		Paquete Grande -> peso > smallMaxWeight
	 * 		Paquete Pequeño -> peso < smallMaxWeight
	 * Caracteristicas de Negocio:
	 * Un paquete puede ocupar un maximo de maxDaysinLocker dias en un compartimento (contando entrega)
	 * Pasado este tiempo, podra ser ocupado su compartimento por otro Paquete
	 * El armario DEBE EXISTIR
	 * 
	 * 
	 * 
	 */
	public int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		Locker t;
		int hueco;
		List<Parcel> paquetes;
		List<Parcel> paquetesPorDia;
		int huecosOcupados;
		
		try {
			tx.begin();
			LockerJPADAO lockerdao = new LockerJPADAO(em);
			ParcelJPADAO parceldao = new ParcelJPADAO(em);
			
			t = lockerdao.find(lockerCode);
			
			//EL ARMARIO DEBE EXISTIR
			if (Objects.isNull(t)) {		
				throw new ParcelServiceException("El Locker que busca no existe");
			}
			
			paquetes = t.getParcels();							//recibe una lista de todos los paquetes en el armario
			
			//DEBE TENER UN PESO ADECUADO
			if (pesoAdecuado(parcelWeight)) {					//mira si el peso es adecuado. Depues mirara si es un paquete pequeño o grande
				
				//DIFERENTES VALORES SEGUN SU CLASIFICACION (grande o pequeño)
				
				if (SmallBigParcel(parcelWeight)) {				//true - pequeño
					paquetes = parceldao.limitarPorPeso(paquetes, 0f, ParcelService.SmallMaxWeight);	//sacamos los paquetes que unicamente sean pequeños
					hueco = t.getSmallCompartments();	//miramos el numero total de huecos para paquetes pequeños
					huecosOcupados = 0;  //primeramente haremos como que nada esta ocupado
					
				} else {								//false - grande
					paquetes = parceldao.limitarPorPeso(paquetes, Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY), ParcelService.LargeMaxWeight);  //sacamos los paquetes que unicamente sean grandes
					hueco = t.getLargeCompartments();	//miramos el numero total de huecos para paquetes grandes
					huecosOcupados = 0;				//primeramente haremos como que nada esta ocupado
				}	
				
				//miraremos para cada dia que ocupe el paquete cuantos paquetes ya hay ocupados
				//nos quedaremos con el maximo de este numero
				//y restado al numero de huecos, sera el espacio verdadero que tendremos para este paquete

				for (int i = 0; i < ParcelService.MaxDaysInLocker; i ++) {
					paquetesPorDia = parceldao.limitarPorFecha(paquetes, date.minusDays(ParcelService.MaxDaysInLocker - 1 - i), date.plusDays(i));
					if (paquetesPorDia.size() > huecosOcupados) {
						huecosOcupados = paquetesPorDia.size();
					}
				}
				return hueco-huecosOcupados;  //el resultado sera el numero posible de paquetes, menos los que ya haya dentro
				
			} else {	//Si el peso NO es ADECUADO
				throw new ParcelServiceException("El peso del paquete no se encuentra en el rango adecuado!! Recuerde que debe estar entre 0 y 10kg");
			}
		} catch (DAOException e) {
			throw new ParcelServiceException(e.getMessage());
		} finally {
			em.close();
		}
	}

	/* deliverParcel - crea una nueva entrega de un paquete con el destinatario, peso y fecha de entrega
	 * Reglas de negocio:
	 * 		Debe haber compartimento disponible (aviableCompartments)
	 * 		Un mismo destinatario no puede tener mas de maxParcelsInLocker compartimentos ocupados simultaneamente en un locker
	 * 		(puede tener varios paquetes para diferentes dias si estos no son a la vez)
	 * 		Un destinatario no puede tener mas de maxParcelsAnywhere compartimetos ocupados simultaneamente en total entre los armarios
	 * 		(puede tener varirios paquetes para diferentes dias si estos no son a la vez)
	 * 
	 * 
	 * 
	 */
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		Parcel p = null;
		Locker l;
		List<Parcel> paquetes;
		List<Parcel> li;
		
		try {
			tx.begin();
			LockerJPADAO lockerdao = new LockerJPADAO(em);
			ParcelJPADAO parceldao = new ParcelJPADAO(em);
			
			paquetes = parceldao.findAll();	//recogemos todos los paquetes (vamos a trabajar con dicha lista)
		
			if (availableCompartments(lockerCode, arrivalDate, weight) > 0) {	//miramos que haya hueco para el paquete
				
				paquetes = parceldao.limitarPorUsuario(paquetes, addressee);
				paquetes = parceldao.limitarPorFecha(paquetes, arrivalDate.minusDays(ParcelService.MaxDaysInLocker+1), arrivalDate.plusDays(ParcelService.MaxDaysInLocker-1));
				
				if (paquetes.size() < ParcelService.MaxParcelsAnywhere) {	//miramos que el usuario no tenga mas de MaxParcelAnywhere paquetes
					
					li = parceldao.limitarPorLocker(paquetes, lockerCode);		//limitamos por lcoker para mirar que no supere el maximo por locker

					if (li.size() >= ParcelService.MaxParcelsInLocker) {
						throw new ParcelServiceException("Este usuario ya tiene el máximo numero de Parcels en dicho Locker");
					}
						
					l = lockerdao.find(lockerCode);
					p = initializeParcelValues(l, addressee, weight, arrivalDate);
					
					parceldao.create(p);
					em.getTransaction().commit();
					em.refresh(l);
						
				} else {
					em.getTransaction().rollback();
					throw new ParcelServiceException("Este usuario ya tiene el máximo número de Parcels en total");
				}
			
			} else {
				em.getTransaction().rollback();
				throw new ParcelServiceException ("No queda suficiente espacio disponible en el Locker para crear dicho paquete");
			}
				
		} catch (DAOException e) {
			try {
			em.getTransaction().rollback();
			throw new ParcelServiceException(e.getMessage());
			} catch (Exception ex) {
				ex.printStackTrace(); 			//error fuera de lo normal
			}
		} finally {
		em.close();
		}
		
		return p;
	}
	

	@Override
	public void retrieveParcel(Long parcelCode) {
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		boolean error = false;
		String msg = "";
		
		try {
			tx.begin();
			ParcelJPADAO parceldao = new ParcelJPADAO(em);
			
			Locker l;
			Parcel p;
			
			p = parceldao.find(parcelCode);
			l = p.getLocker();
			
			//si el dia que llego + el maximo tiempo en un locker es antes o igual a hoy, esta caducado
			if ((p.getArrivalDate().plusDays(ParcelService.MaxDaysInLocker).isBefore(LocalDate.now()) || p.getArrivalDate().plusDays(ParcelService.MaxDaysInLocker).isEqual(LocalDate.now()))) {
				error = true;
				msg = "El Parcel no puede recogerse porque está caducado";
			}
			//si el paquete aun no ha llegado al locker
			if (p.getArrivalDate().isAfter(LocalDate.now())) {
				error = true;
				msg = "El Parcel no puede recogerse porque no esta actualmente en el Locker. Aun no ha llegado!";
			}
			
			if (error == true) {
				em.getTransaction().rollback();
				throw new ParcelServiceException(msg);
			}
			
			//eliminamos cuando no haya ocurrido ningun error
			
			parceldao.delete(p);		//eliminamos el parcel
			
			em.getTransaction().commit();
			em.refresh(l);
			
		} catch (DAOException e) {
			try {
			em.getTransaction().rollback();
			throw new ParcelServiceException(e.getMessage());
			} catch (Exception ex) {
				ex.printStackTrace();  				//error fuera de lo normal
			}
		}
		
	}

	//METODOS PRIVADOS DE JPAParcelService
	
	//Permite ver si el peso es adecuado
	private boolean pesoAdecuado(float parcelWeight) {
		boolean adecuado = true;
		if (parcelWeight <= 0 || parcelWeight > ParcelService.LargeMaxWeight) {
			adecuado = false;
		}
		return adecuado;
	}
	
	//permite ver si es un parcel que ocuparia un peso pequeño o grande (true = pequeño, false = grande)
	private boolean SmallBigParcel(float parcelWeight) {
		boolean small = true;
		if (parcelWeight > ParcelService.SmallMaxWeight) {
			small = false;
		}
		return small;
	}
	
	//inicializa los paramtros de un Parcel
	private Parcel initializeParcelValues(Locker lock, int addressee, float weight, LocalDate arrivalDate) {
		Parcel t = new Parcel();
		t.setAddressee(addressee);
		t.setArrivalDate(arrivalDate);
		t.setLocker(lock);
		t.setWeight(weight);
		
		return t;
	}
	
	//inicializa los parametros de un locker
	private Locker initializeLockerValues (String name, String address, double longitude, double latitude, int largeCompartments, int smallCompartments) {
		String errMsg = null;
		boolean error = false;
		
		Locker t = new Locker();
		
		if (name == "" || name == null || name.isEmpty()) {
			errMsg = "El nombre del armario no puede ser nulo. Introduzca un dato válido";
			error = true;
		} else if(address == "" || address == null || address.isEmpty()) {
			errMsg = "El address del armario no puede ser nulo. Introduzca un dato válido";
			error = true;
		} else if(longitude < -180 || longitude > 180) {
			errMsg = "Longitude introducido incorrectamente! Recuerde que tiene que ser un valor entre -180 y 180";
			error = true;
		} else if(latitude < -90 || latitude > 90) {
			errMsg = "Latitude introducido incorrectamente! Recuerde que tiene que ser un valor entre -90 y 90";
			error = true;
		} else if(largeCompartments < 0 || smallCompartments < 0 ) {
			errMsg = "El numero de compartimentos es incorrecto! Recuerde que tiene que ser un valor positivo";
			error = true;
		}
		
		if (error == true) {
			throw new ParcelServiceException (errMsg);
		}	
		
		t.setAddress(address);
		t.setLargeCompartments(largeCompartments);
		t.setLatitude(latitude);
		t.setLongitude(longitude);
		t.setName(name);
		t.setSmallCompartments(smallCompartments);
		
		return t;
	}
}

package paa.locker.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paa.locker.model.Locker;
import paa.locker.model.Parcel;

public class RemoteParcelService implements ParcelService {
	String server_port = "localhost:8080";
	String name_servlet = "/p4-servidor/LockerServer";
	@Override
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,
			int smallCompartments) {
		
		ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
		URL url;
		Locker l = null;
		
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=createLocker&name=" + name + "&address=" + 
				address + "&longitude=" + longitude + "&latitude=" + latitude + "&largeCompartments=" + largeCompartments + 
				"&smallCompartments=" + smallCompartments);
		
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			String message = "";
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			      String line;
			      
			      while ((line = bufferedReader.readLine()) != null) {
			         message = message.concat(line);
			      }
			    }
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			l = objm.readValue(message, Locker.class);
			connection.disconnect();
			
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		}
		
		return l;
	}

	@Override
	public Locker findLocker(Long lockerCode) {
		ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
		URL url;
		Locker l = null;
		
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=findLocker&lockerCode=" + lockerCode);
			
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			String message = "";
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			      String line;
			      
			      while ((line = bufferedReader.readLine()) != null) {
			         message = message.concat(line);
			      }
			    }
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			l = objm.readValue(message, Locker.class);
			connection.disconnect();
			
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		}
		
		return l;
	}

	@Override
	public List<Locker> findAllLockers() {
		ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
		URL url;
		List<Locker> lista = new ArrayList<Locker> ();
		
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=findAllLockers");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			String message = "";
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			      String line;
			      
			      while ((line = bufferedReader.readLine()) != null) {
			         message = message.concat(line);
			      }
			    }
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			lista = objm.readValue(message , new TypeReference<List<Locker>>(){});
			connection.disconnect();
		
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
		}
		
		return lista;
		
	}

	@Override
	public int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight) {
		ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
		URL url;
		int n = 0;
		
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=aviableCompartments&lockerCode=" + lockerCode + 
					"&date=" + date + "&parcelWeight=" + parcelWeight);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			String message = "";
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			      String line;
			      
			      while ((line = bufferedReader.readLine()) != null) {
			         message = message.concat(line);
			      }
			    }
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			n = objm.readValue(message , Integer.class);
			connection.disconnect();
			
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
			
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		}
		
		return n;
	}

	@Override
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
		ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
		URL url;
		Parcel p = null;
		
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=deliverParcel&lockerCode=" + lockerCode + 
					"&addressee=" + addressee + "&weight=" + weight + "&arrivalDate=" + arrivalDate);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			String message = "";
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			      String line;
			      
			      while ((line = bufferedReader.readLine()) != null) {
			         message = message.concat(line);
			      }
			    }
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			p = objm.readValue(message , Parcel.class);
			connection.disconnect();
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		}
		
		return p;
	}
	@Override
	public void retrieveParcel(Long parcelCode) {
		
		URL url;
		try {
			url = new URL ("http://" + server_port + name_servlet + "?accion=retrieveParcel&parcelCode=" + parcelCode);
			
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//void
			  } else {
				throw new ParcelServiceException(connection.getHeaderField("msg"));
			}
			
			connection.disconnect();
			
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			throw new ParcelServiceException(e.getMessage());
		} catch (ParcelServiceException ex) {
			System.out.println(ex.getStackTrace());
			throw new ParcelServiceException(ex.getMessage());
		}

	}

}

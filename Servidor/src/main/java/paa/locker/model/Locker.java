package paa.locker.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class, 
		property = "code")
public class Locker implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
    private Long   code;
    private String name;
    private String address;	//direccion postal donde se encuentra
    private double longitude, latitude;
    private int largeCompartments, smallCompartments; //numero de compartimentos de cada tipo
    
    @OneToMany(mappedBy="locker")
    private List<Parcel> parcels;

    public Locker() {}  //contructor sin parametros (el otro lo borré)
    
    //NECESARIO PARA PRACTICA 3
    public Locker(Long code, String name, String address, double longitude, double latitude , int largeCompartments, int smallCompartments) {
    	this.code = code;
    	this.name = name;
    	this.address = address;
    	this.longitude = longitude;
    	this.latitude = latitude;
    	this.largeCompartments = largeCompartments;
    	this.smallCompartments = smallCompartments;
    }

    public Long getCode() {
        return code;
    }

    @Override
	public String toString() {
		return "Locker " + code + " in " + address;
	}

	public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLargeCompartments() {
        return largeCompartments;
    }

    public void setLargeCompartments(int largeCompartments) {
        this.largeCompartments = largeCompartments;
    }

    public int getSmallCompartments() {
        return smallCompartments;
    }

    public void setSmallCompartments(int smallCompartments) {
        this.smallCompartments = smallCompartments;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locker)) return false;

        Locker locker = (Locker) o;

        return Objects.equals(code, locker.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

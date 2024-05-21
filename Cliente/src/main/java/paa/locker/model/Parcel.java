package paa.locker.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class, 
		property = "code")
public class Parcel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

    private Long      code;
    private int       addressee; //DNI del destinatario
    float             weight;
    private LocalDate arrivalDate;


    private Locker locker;

    public Parcel() {}

    public Parcel(Long code, int addressee, float weight, LocalDate arrivalDate, Locker locker) {
        this.code = code;
        this.addressee = addressee;
        this.weight    = weight;
        this.arrivalDate = arrivalDate;
        this.locker = locker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parcel)) return false;

        Parcel parcel = (Parcel) o;

        return Objects.equals(code, parcel.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public int getAddressee() {
        return addressee;
    }

    public void setAddressee(int addressee) {
        this.addressee = addressee;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return code + ": " + weight + " kg parcel for Locker: " + 
    (locker != null ? locker.getCode() : null) + " (arr. " + arrivalDate + ")"; 
      
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }
}

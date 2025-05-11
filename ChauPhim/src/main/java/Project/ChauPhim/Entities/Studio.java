package Project.ChauPhim.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Studio",
		uniqueConstraints = @UniqueConstraint(name = "Studio_name_key", columnNames = "name"))
public class Studio {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "studioID", nullable = false)
	private Long studioID;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "country", nullable = true)
	private String country;
	
	@Column(name = "year", nullable = true)
	private int year;

	public Long getStudioID() {
		return studioID;
	}

	public void setStudioID(Long studioID) {
		this.studioID = studioID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
}

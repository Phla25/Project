package com.project.ChauPhim.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Studio")
public class Studio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "studioID", nullable = false)
	private Long studioID;
	
	@Column(name = "name", length = 256, nullable = false)
	private String name;
	
	@Column(name = "year", nullable = true)
	private int year;
	
	@Column(name = "country", length = 256, nullable = true)
	private String country;
	
	@Column(name = "address", length = 256, nullable = true)
	private String address;
	
	@Column(name = "email", length = 256, nullable = true)
	private String email;
	
	@Column(name = "phonenumber", length = 32, nullable = true)
	private String phonenumber;

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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
}

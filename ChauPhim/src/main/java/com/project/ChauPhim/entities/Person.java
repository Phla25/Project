package com.project.ChauPhim.entities;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Person")
public class Person {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "personID", nullable = false)
	private Long personID;
	
	@Column(name = "name", length = 100, nullable = false)
	private String name;
	
	@Column(name = "DOB", nullable = true)
	private Date DOB;
	
	@Column(name = "gender", nullable = true)
	private int gender;
	
	@Column(name = "age", nullable = true)
	private int age;
	
	@Column(name = "nationality", length = 100, nullable = true)
	private String nationality;

	public Long getPersonID() {
		return personID;
	}

	public void setPersonID(Long personID) {
		this.personID = personID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDOB() {
		return DOB;
	}

	public void setDOB(Date dOB) {
		DOB = dOB;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
}

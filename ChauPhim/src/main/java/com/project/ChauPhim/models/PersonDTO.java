package com.project.ChauPhim.models;

import java.util.Date;

public class PersonDTO {
	protected Long personID;
	protected String name;
	protected Date DOB;
	protected int gender;
	protected int age;
	protected String nationality;
	
	public PersonDTO(){	
		super();
	}
	
	public PersonDTO(String name) {
		super();
		this.name = name;
	}

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

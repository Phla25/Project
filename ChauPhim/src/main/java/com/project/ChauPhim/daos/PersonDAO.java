package com.project.ChauPhim.daos;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.ChauPhim.entities.Person;
import com.project.ChauPhim.models.PersonDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class PersonDAO {
	@Autowired
	private EntityManager entityManager;
	
	public void addPersonToDataBase(PersonDTO person) {
		entityManager.persist(person);
	}
	public Person findPersonByName(String name) {
		String sql = "Select e from " + Person.class.getName() + " e "
				+ "where e.name =: name";
		Query query = entityManager.createQuery(sql, PersonDTO.class);
		return (Person) query.getSingleResult();
	}
	// Cái này nên để ở service
	public void updatePersonName(String name1, String name2) {
		Person p = this.findPersonByName(name1);
		p.setName(name2);
	}
}

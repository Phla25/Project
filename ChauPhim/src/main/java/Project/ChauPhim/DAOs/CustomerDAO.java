package Project.ChauPhim.DAOs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Repository
public class CustomerDAO {
	@Autowired
	private EntityManager entityManager;
	
	public Customer findByUserName(String username) {
		String sql = "Select e from " + Customer.class.getName()
				+ " e where e.username =: username";
		Query query = entityManager.createQuery(sql, Customer.class);
		query.setParameter("username", username);
		return (Customer) query.getSingleResult();
	}
	
}

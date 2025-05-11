package Project.ChauPhim.DAOs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Manager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Repository
public class ManagerDAO {
	@Autowired
	private EntityManager entityManager;
	
	public Manager findByUserName(String username) {
		String sql = "Select e from " + Manager.class.getName()
				+ " e where e.username =: username";
		Query query = entityManager.createQuery(sql, Manager.class);
		query.setParameter("username", username);
		return (Manager) query.getSingleResult();
	}
	
}

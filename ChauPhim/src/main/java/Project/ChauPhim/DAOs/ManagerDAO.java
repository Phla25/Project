package Project.ChauPhim.DAOs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Manager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

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
	@Transactional
	public void addManager(Manager manager) {
		// Tạo câu lệnh SQL gốc
        String sql = "INSERT INTO \"Manager\" (\"username\", \"password\", \"email\") VALUES (?, ?, ?)";
        
        // Sử dụng createNativeQuery và truyền tham số
        entityManager.createNativeQuery(sql)
                     .setParameter(1, manager.getUsername()) // Tham số đầu tiên
                     .setParameter(2, manager.getPassword()) // Tham số thứ hai
                     .setParameter(3, manager.getEmail())    // Tham số thứ ba
                     .executeUpdate(); // Thực thi câu lệnh
	}
	public boolean existsByUsername(String username) {
	    String queryStr = "SELECT COUNT(m) FROM Manager m WHERE m.username = :username";
	    Query query = entityManager.createQuery(queryStr);
	    query.setParameter("username", username);

	    Long count = (Long) query.getSingleResult();
	    return count > 0;
	}
}

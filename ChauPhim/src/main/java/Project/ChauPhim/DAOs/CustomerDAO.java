package Project.ChauPhim.DAOs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

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

	public void addCustomer(Customer customer) {
		// Tạo câu lệnh SQL gốc
        String sql = "INSERT INTO \"Customer\" (\"username\", \"password\", \"email\", \"name\", \"dob\", \"rank\") VALUES (?, ?, ?, ?, ?, ?)";
        
        // Sử dụng createNativeQuery và truyền tham số
        entityManager.createNativeQuery(sql)
                     .setParameter(1, customer.getUsername()) // Tham số đầu tiên
                     .setParameter(2, customer.getPassword()) // Tham số thứ hai
                     .setParameter(3, customer.getEmail())    // Tham số thứ ba
                     .setParameter(4, customer.getName())
                     .setParameter(5, customer.getDob())
                     .setParameter(6, customer.getRank())
                     .executeUpdate(); // Thực thi câu lệnh
	}

	@Transactional
	public void updateEmailAndName(String username, String newEmail, String newName) {
    String sql = "UPDATE \"Customer\" SET \"email\" = ?, \"name\" = ? WHERE \"username\" = ?";
	entityManager.createNativeQuery(sql)
             .setParameter(1, newEmail)
             .setParameter(2, newName)
             .setParameter(3, username)
             .executeUpdate();

	}
	
}

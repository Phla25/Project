package Project.ChauPhim.DAOs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
		@SuppressWarnings("unchecked")
		List<Customer> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
	}

    @SuppressWarnings("unchecked")
    public List<Customer> findAllCustomers() {
        String sql = "SELECT * FROM \"Customer\"";
        Query query = entityManager.createNativeQuery(sql, Customer.class);
        List<Customer> customers = query.getResultList();
        return customers;
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
    public void updateCustomer(String username, String email, String name, 
                              String gender, String nationality, LocalDate dob) {
        
        // Tạo câu lệnh SQL động dựa trên các trường không null
        StringBuilder sqlBuilder = new StringBuilder("UPDATE \"Customer\" SET ");
        
        // Danh sách tham số và giá trị sẽ được sử dụng
        List<String> updateFields = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        
        // Kiểm tra và thêm các trường cần cập nhật
        if (email != null && !email.trim().isEmpty()) {
            updateFields.add("\"email\" = ?");
            paramValues.add(email);
        }
        
        if (name != null && !name.trim().isEmpty()) {
            updateFields.add("\"name\" = ?");
            paramValues.add(name);
        }
        
        if (gender != null && !gender.trim().isEmpty()) {
            updateFields.add("\"gender\" = ?");
            paramValues.add(gender);
        }
        
        if (nationality != null && !nationality.trim().isEmpty()) {
            updateFields.add("\"nationality\" = ?");
            paramValues.add(nationality);
        }
        
        if (dob != null) {
            updateFields.add("\"dob\" = ?");
            paramValues.add(dob);
        }
        
        // Nếu không có trường nào cần cập nhật, trả về
        if (updateFields.isEmpty()) {
            return;
        }
        
        // Hoàn thành câu lệnh SQL
        sqlBuilder.append(String.join(", ", updateFields));
        sqlBuilder.append(" WHERE \"username\" = ?");
        paramValues.add(username);
        
        // Tạo câu truy vấn và thiết lập tham số
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        
        // Thiết lập các tham số
        for (int i = 0; i < paramValues.size(); i++) {
            query.setParameter(i + 1, paramValues.get(i));
        }
        
        // Thực thi câu truy vấn
        query.executeUpdate();
    }
	// viết 1 hàm updateCustomer và update tất cả các field, trường nào bị bỏ trống trong form điền thì không update 
    
    @Transactional
    public void updateRank(String username, int rank) {
        if (username == null || rank != 0 && rank != 1 && rank != 2) {
        throw new IllegalArgumentException("Username và rank không hợp lệ!");
    }
    
    String sql = "UPDATE \"Customer\" SET \"rank\" = ? WHERE \"username\" = ?";
    
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter(1, rank);
    query.setParameter(2, username);
    
    int updatedRows = query.executeUpdate();
    
    if (updatedRows == 0) {
        throw new RuntimeException("Không tìm thấy khách hàng với username: " + username);
    	}
    }

    @Transactional
    public BigDecimal getCustomerBalance(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username không được null");
        }

        String sql = "SELECT \"balance\" FROM \"Customer\" WHERE \"username\" = ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, username);

        Object result = query.getSingleResult();

        if (result == null) {
            throw new RuntimeException("Không tìm thấy khách hàng với username: " + username);
        }
        System.out.println("result: " + result);
        // Chuyển đổi từ Double sang BigDecimal
        if (result instanceof Double) {
            return new BigDecimal(result.toString());
        } else if (result instanceof BigDecimal) {
            return (BigDecimal) result;
        } else {
            // Trường hợp khác, chuyển đổi an toàn
            return new BigDecimal(result.toString());
        }
    }

    @Transactional
    public void updateBalance(String username, BigDecimal newBalance) {
        if (username == null || newBalance == null) {
            throw new IllegalArgumentException("Username và balance không được null");
        }

        String sql = "UPDATE \"Customer\" SET \"balance\" = ? WHERE \"username\" = ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, newBalance);
        query.setParameter(2, username);

        int updatedRows = query.executeUpdate();

        if (updatedRows == 0) {
            throw new RuntimeException("Không tìm thấy khách hàng với username: " + username);
        }
    }

    public Customer findById(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }

        String sql = "SELECT * FROM \"Customer\" WHERE \"customerID\" = ?";

        Query query = entityManager.createNativeQuery(sql, Customer.class);
        query.setParameter(1, customerId);

        try {
            Customer result = (Customer) query.getSingleResult();
            return result;
        } catch (NoResultException e) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId);
        }
    }
}
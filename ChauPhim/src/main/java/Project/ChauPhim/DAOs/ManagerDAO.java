package Project.ChauPhim.DAOs;

import java.util.ArrayList;
import java.util.List;

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
	public Long countUser() {
	    String queryStr = "SELECT COUNT(*) FROM \"Customer\"";
	    Query query = entityManager.createNativeQuery(queryStr);

	    Long count = (Long) query.getSingleResult();
	    return count;
	}
	public Long countDiscount() {
		String queryStr = "SELECT COUNT(*) FROM \"Discount\"";
	    Query query = entityManager.createNativeQuery(queryStr);

	    Long count = (Long) query.getSingleResult();
	    return count;
	}
	public Long countSold2Day() {
		String sql = "SELECT COUNT(*) FROM \"Orders\" WHERE date = current_date";
		Long count = (Long) entityManager.createNativeQuery(sql).getSingleResult();
		return count;
	}
	public Long countSoldMonth(int month) {
		String sql = "SELECT COUNT(*) FROM \"Orders\" WHERE extract(month from date) = ?";
		Long count = (Long) entityManager.createNativeQuery(sql).setParameter(1, month).getSingleResult();
		return count;
	}
	public Long countSoldYear(int year) {
		String sql = "SELECT COUNT(*) FROM \"Orders\" WHERE extract(year from date) = ?";
		Long count = (Long) entityManager.createNativeQuery(sql).setParameter(1, year).getSingleResult();
		return count;
	}
	public void updateManager(String username, String email) {
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
}

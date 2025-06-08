package Project.ChauPhim.DAOs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // Lưu order mới
    @Transactional
    public void save(Orders order) {
        entityManager.persist(order);
    }

    // Tìm tất cả orders của 1 customer
    @SuppressWarnings("unchecked")
    public List<Orders> findByCustomerID(Long customerID) {
        String sql = "SELECT * FROM \"Orders\" WHERE \"customerID\" = :customerID ORDER BY \"date\" DESC";
        Query query = entityManager.createNativeQuery(sql, Orders.class);
        query.setParameter("customerID", customerID);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Orders> findAllOrders() {
        String sql = "SELECT * FROM \"Orders\"";
        Query query = entityManager.createNativeQuery(sql, Orders.class);
        return query.getResultList();
    }

    // Tìm order theo ID
    public Orders findById(Long orderID) {
        return entityManager.find(Orders.class, orderID);
    }

    // Kiểm tra customer đã mua phim này chưa
    public Orders findByCustomerIdAndMovieId(Long customerID, Long movieID) {
        try {
            String sql = "SELECT * FROM \"Orders\" WHERE \"customerID\" = :customerID AND \"movieID\" = :movieID";
            Query query = entityManager.createNativeQuery(sql, Orders.class);
            query.setParameter("customerID", customerID);
            query.setParameter("movieID", movieID);
            return (Orders) query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Chưa mua phim này
        }
    }

    // Đếm tổng số phim đã mua của customer
    public int countMoviesByCustomerID(Long customerID) {
        String sql = "SELECT COUNT(*) FROM \"Orders\" WHERE \"customerID\" = :customerID";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("customerID", customerID);
        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }

    // Tính tổng tiền đã chi của customer
    public double calculateTotalSpentByCustomerID(Long customerID) {
        String sql = "SELECT COALESCE(SUM(m.\"price\"), 0) " +
                    "FROM \"Orders\" o " +
                    "JOIN \"Movie\" m ON o.\"movieID\" = m.\"movieID\" " +
                    "WHERE o.\"customerID\" = :customerID";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("customerID", customerID);
        Number result = (Number) query.getSingleResult();
        return result.doubleValue();
    }

    // Lấy danh sách phim phổ biến nhất (được mua nhiều nhất)
    @SuppressWarnings("unchecked")
    public List<Object[]> findMostPopularMovies(int limit) {
        String sql = "SELECT m.\"movieID\", m.\"title\", COUNT(*) as purchase_count " +
                    "FROM \"Orders\" o " +
                    "JOIN \"Movie\" m ON o.\"movieID\" = m.\"movieID\" " +
                    "GROUP BY m.\"movieID\", m.\"title\" " +
                    "ORDER BY purchase_count DESC " +
                    "LIMIT :limit";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);
        return query.getResultList();
    }

    // Cập nhật rating
    @Transactional
    public void updateRating(Long orderID, int rating) {
        String sql = "UPDATE \"Orders\" SET \"rate\" = :rating WHERE \"orderID\" = :orderID";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("rating", rating);
        query.setParameter("orderID", orderID);
        query.executeUpdate();
    }
    // Tìm orders trong khoảng thời gian
    @SuppressWarnings("unchecked")
    public List<Orders> findByCustomerIDAndDateRange(Long customerID, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM \"Orders\" WHERE \"customerID\" = :customerID " +
                    "AND \"date\" BETWEEN :startDate AND :endDate " +
                    "ORDER BY \"date\" DESC";
        Query query = entityManager.createNativeQuery(sql, Orders.class);
        query.setParameter("customerID", customerID);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
}

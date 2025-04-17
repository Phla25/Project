import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String url = "jdbc:postgresql://localhost:5432/dellstore";
        String user = "admin_dell";  // đổi nếu bạn dùng tài khoản khác
        String password = "12212332"; // đổi theo DB bạn

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL!");
            String sql = "SELECT prod_id, title, price FROM products limit 10;";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("List of first 10 products:");
                while (rs.next()) {
                    int id = rs.getInt("prod_id");
                    String title = rs.getString("title");
                    double price = rs.getDouble("price");

                    System.out.printf("ID: %d | Name: %s | Price: %.2f\n", id, title, price);
                }
            }
            catch (SQLException e) {
                System.out.println("Error in connection!");
                e.printStackTrace();
            }
        }
    }
}

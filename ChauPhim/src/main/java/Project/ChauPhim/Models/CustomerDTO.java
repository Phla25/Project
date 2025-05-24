package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDTO extends AppUser{
	private int rank = 0; // 0: normal, 1: silver, 2: gold, etc.
    // private int totalExperience; => Dựa vào số lượng phim mua để tính rank
    private List<CartDTO> cartHistory = new ArrayList<CartDTO>();	// Danh sách các Cart đã đặt từ trước tới giờ
    private List<OrderDTO> orderHistory = new ArrayList<OrderDTO>(); // Danh sách các Order của người dùng từ trước tới giờ
    private LocalDate dob;
	private Long customerID;
	public CustomerDTO(Long customerID, String username, String password, String email, int rank, LocalDate dob) {
		super(username, password, email);
		this.customerID = customerID;
		this.rank = rank;
		this.dob = dob;
	}
	public Long getCustomerID() {
		return customerID;
	}
	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}
	public int getRank() {
		return this.rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public List<OrderDTO> getOrderHistory() {
		return orderHistory;
	}
	public void setOrderHistory(List<OrderDTO> orderHistory) {
		this.orderHistory = orderHistory;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public List<CartDTO> getCartHistory() {
		return cartHistory;
	}
	public void setCartHistory(List<CartDTO> cartHistory) {
		this.cartHistory = cartHistory;
	}
    
    
}

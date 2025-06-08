package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Project.ChauPhim.Entities.Customer;

public class CustomerDTO extends AppUser{
	private int rank = 0; // 0: normal, 1: silver, 2: gold, etc.
    // private int totalExperience; => Dựa vào số lượng phim mua để tính rank
    private List<OrderDTO> orderHistory = new ArrayList<OrderDTO>(); // Danh sách các Order của người dùng từ trước tới giờ
    private LocalDate dob;
	private Long customerID;

	public CustomerDTO(Customer customer) {
        super(customer.getUsername(), null, customer.getEmail());
		this.customerID = customer.getCustomerID();
		this.rank = customer.getRank();
		this.dob = customer.getDob();
	}

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
}

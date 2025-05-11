package Project.ChauPhim.Models;

import java.util.ArrayList;
import java.util.List;
import Project.ChauPhim.Entities.Order;

public class CustomerDTO extends AppUser{
	private int rank = 0; // 0: normal, 1: silver, 2: gold, etc.
    // private int totalExperience; => Dựa vào số lượng phim mua để tính rank
    private List<Order> cart = new ArrayList<Order>();	// Danh sách các Order trong Cart đấy
    private List<Order> orderHistory = new ArrayList<Order>(); // Danh sách các Order của người dùng từ trước tới giờ
    
    
	public CustomerDTO(String username, String password, String email, int rank) {
		super(username, password, email);
		// TODO Auto-generated constructor stub
		this.rank = rank;
	}
	public int getRank() {
		return this.rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public List<Order> getCart() {
		return cart;
	}
	public void setCart(List<Order> cart) {
		this.cart = cart;
	}
	public List<Order> getOrderHistory() {
		return orderHistory;
	}
	public void setOrderHistory(List<Order> orderHistory) {
		this.orderHistory = orderHistory;
	}
    
    
}

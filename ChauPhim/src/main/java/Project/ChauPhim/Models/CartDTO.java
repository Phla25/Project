package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CartDTO {
	private List<OrderDTO> orders = new ArrayList<OrderDTO>();	// Danh sách các sản phẩm trong Cart
	private CustomerDTO customer;
	private int status; // 0 - chua dat; 1 - da dat; 2 - da thanh toan
	// private double finalPrice; Viết hàm tự tính được
	private LocalDate date;
	
	public CartDTO(CustomerDTO customer, int status, LocalDate date) {
		super();
		this.customer = customer;
		this.status = status;
		this.date = date;
	}
	public List<OrderDTO> getOrders() {
		return orders;
	}
	public void setOrders(List<OrderDTO> orders) {
		this.orders = orders;
	}
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}

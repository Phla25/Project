package chauphim.entity.cart;

import chauphim.entity.person.Customer;
import chauphim.entity.movie.Movie;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class Cart {

	private int cartID;
	private List<Order> orders = new ArrayList<Order>();
	private Customer customer;
	private int status; // 0 - chua dat; 1 - da dat; 2 - da thanh toan
	private double finalPrice; 
	private LocalDate date;

	public Cart(Customer customer) {
		// TODO gán cartID (từ database)
		this.customer = customer;
		this.status = 0;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void addOrder(Order order) {
		orders.add(order);
	}

	public void removeOrder(Order order) {
		orders.remove(order);
	}

	public double calculateTotal() {
		double total = 0;
		for (Order order : orders) {
			total += order.totalPrice();
		}
		return total;
	}

	// dat hang
	public Cart setCart() {
		if (this.status == 0)
		{
			this.status = 1;
			this.date = LocalDate.now();
			this.finalPrice = calculateTotal();
			Cart newCart = new Cart(this.customer);
			return newCart;
		}
		else
		{
			return null;
		}
	}

	// chot don hang
	public void confirmCart() {
		if (this.status == 1) {
			this.status = 2;
			this.date = LocalDate.now();
		}
	}

	public boolean searchInCart(Movie movie) {
		for (Order order : orders) {
			if (order.getMovie().equals(movie)) {
				return true;
			}
		}
		return false;
	}

}

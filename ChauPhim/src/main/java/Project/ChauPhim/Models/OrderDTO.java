package Project.ChauPhim.Models;

import java.time.LocalDate;

public class OrderDTO {
    private MovieDTO movie;	// NOT NULL
    private int quantity;	// NOT NULL
    private LocalDate date;	// NOT NULL
    private CustomerDTO customer;	// NOT NULL
    private int rate;
    private DiscountDTO discount;
    private double totalPrice;
	public OrderDTO(MovieDTO movie, LocalDate date, CustomerDTO customer, int rate, DiscountDTO discount,
			double totalPrice) {
		super();
		this.movie = movie;
		this.date = date;
		this.customer = customer;
		this.rate = rate;
		this.discount = discount;
		this.totalPrice = totalPrice;
	}
	public MovieDTO getMovie() {
		return movie;
	}
	public void setMovie(MovieDTO movie) {
		this.movie = movie;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public DiscountDTO getDiscount() {
		return discount;
	}
	public void setDiscount(DiscountDTO discount) {
		this.discount = discount;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	} 
}

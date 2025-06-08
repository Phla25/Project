package Project.ChauPhim.Models;

import java.time.LocalDate;

public class OrderDTO {
    private MovieDTO movie;	// NOT NULL
    private LocalDate date;	// NOT NULL
    private CustomerDTO customer;	// NOT NULL
    private int rate;
    private double totalPrice;
	private Long customerID;

	public OrderDTO(MovieDTO movie, LocalDate date, CustomerDTO customer, int rate,
			double totalPrice, Long customerID) {
		super();
		this.movie = movie;
		this.date = date;
		this.customer = customer;
		this.rate = rate;
		this.totalPrice = totalPrice;
		this.customerID = customerID;
	}
	
	public OrderDTO() {}
	public Long getCustomerID() {
		return customerID;
	}
	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}
	public MovieDTO getMovie() {
		return movie;
	}
	public void setMovie(MovieDTO movie) {
		this.movie = movie;
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
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	} 
}
package Project.ChauPhim.Entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Orders\"")
public class Orders {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE
	)
	@Column(name = "\"orderID\"", nullable = false)
	private Long orderID;
	
	@Column(name = "\"customerID\"", nullable = false)
	private Long customerID;
	
	@Column(name = "\"movieID\"", nullable = false)
	private Long movieID;
	
	@Column(name = "date" , nullable = false)
	private LocalDate date;
	
	@Column(name = "rate", nullable = true)
	private int rate;
	
	@Column(name = "\"cartID\"", nullable = true)
	private Long cartID;

	public Long getOrderID() {
		return orderID;
	}

	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}

	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	public Long getMovieID() {
		return movieID;
	}

	public void setMovieID(Long movieID) {
		this.movieID = movieID;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Long getCartID() {
		return cartID;
	}

	public void setCartID(Long cartID) {
		this.cartID = cartID;
	}
	
}

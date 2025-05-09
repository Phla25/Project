package com.project.ChauPhim.entities;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Order")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orderID", nullable = false)
	private Long orderID;
		
	@Column(name = "movieID", nullable = false)
	private Long movieID;
	
	@Column(name = "quantity", nullable = false)
	private int quantity;
	
	@Column(name = "order_date", nullable = false)
	private Date order_date;
	
	@Column(name = "customerID", nullable = false)
	private Long customerID;
	
	@Column(name = "rate", nullable = true)
	private int rate;
	
	@Column(name = "discountID", nullable = true)
	private Long discountID;

	public Long getOrderID() {
		return orderID;
	}

	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}

	public Long getMovieID() {
		return movieID;
	}

	public void setMovieID(Long movieID) {
		this.movieID = movieID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Long getDiscountID() {
		return discountID;
	}

	public void setDiscountID(Long discountID) {
		this.discountID = discountID;
	}
	
	
}

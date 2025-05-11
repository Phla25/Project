package Project.ChauPhim.Entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cart")
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cartID", nullable = false)
	private Long cartID;
	
	@Column(name = "customerID", nullable = false)
	private Long customerID;
	
	@Column(name = "date", nullable = false)
	private Date date;
	
	@Column(name = "status", nullable = false)
	private int status;

	public Long getCartID() {
		return cartID;
	}

	public void setCartID(Long cartID) {
		this.cartID = cartID;
	}

	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}

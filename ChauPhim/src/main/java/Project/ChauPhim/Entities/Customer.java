package Project.ChauPhim.Entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "\"Customer\"", 
		uniqueConstraints = @UniqueConstraint(name = "Customer_username_key", columnNames = "username"))
public class Customer {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE
	)
	@Column(name = "\"customerID\"", nullable = false)
	private Long customerID;
	
	@Column(name = "username", length = 256, nullable = false)
	private String username;
	
	@Column(name = "password", length = 256, nullable = false)
	private String password;
	
	@Column(name = "email", length = 256, nullable = false)
	private String email;
	
	@Column(name = "name", length = 128, nullable = false)
	private String name;
	
	@Column(name = "gender", length = 10, nullable = true)
	private String gender;
	
	@Column(name = "nationality", length = 128, nullable = true)
	private String nationality;
	
	@Column(name = "dob", nullable = false)
	private LocalDate dob;
	
	@Column(name = "rank", nullable = false)
	private int rank;

	@Column(name = "balance", nullable = false)
	@ColumnDefault("0::money")
	private BigDecimal balance = BigDecimal.ZERO;
	
	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}
}

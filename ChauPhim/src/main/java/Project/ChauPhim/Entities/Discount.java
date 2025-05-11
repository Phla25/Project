package Project.ChauPhim.Entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Discount",
		uniqueConstraints = @UniqueConstraint(name = "Discount_name_key", columnNames = "name"))
public class Discount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "discountID", nullable = false)
	private Long discountID;
	
	@Column(name = "name", length = 128, nullable = false)
	private String name;
	
	@Column(name = "percentage", nullable = false)
	private int percentage;
	
	@Column(name = "releaseDate", nullable = false)
	private LocalDate releaseDate;
	
	@Column(name = "expireDate", nullable = false)
	private LocalDate expireDate;

	public Long getDiscountID() {
		return discountID;
	}

	public void setDiscountID(Long discountID) {
		this.discountID = discountID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}
	
}

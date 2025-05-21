package Project.ChauPhim.Models;

import java.time.LocalDate;

public class DiscountDTO {
	private String name; //NOT NULL
	private int percentage; //NOT NULL
	private LocalDate releaseDate; //NOT NULL
	private LocalDate expireDate; //NOT NULL
	public DiscountDTO(String name, int percentage, LocalDate releaseDate, LocalDate expireDate) {
		super();
		this.name = name;
		this.percentage = percentage;
		this.releaseDate = releaseDate;
		this.expireDate = expireDate;
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

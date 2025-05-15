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
@Table(name = "\"Actor\"",
		uniqueConstraints = @UniqueConstraint(name = "Actor_imageURL_key", columnNames = "imageURL"))
public class Actor {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "\"actorID\"", nullable = false)
	private Long actorID;
	
	@Column(name = "name", length = 128, nullable = false)
	private String name;
	
	@Column(name = "\"imageURL\"", length = 256, nullable = false)
	private String imageURL;
	
	@Column(name = "gender", length = 10, nullable = true)
	private String gender;
	
	@Column(name = "dob", nullable = true)
	private LocalDate dob;
	
	@Column(name = "rank", nullable = true)
	private int rank;
	
	@Column(name = "bio", nullable = true)
	private String bio;

	public Long getActorID() {
		return actorID;
	}

	public void setActorID(Long actorID) {
		this.actorID = actorID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
}

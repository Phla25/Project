package com.project.ChauPhim.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Director")
public class Director {
	@Id
	@Column(name = "directorID", nullable = false)
	private Long directorID;
	
	@Column(name = "rank", nullable = true)
	private int rank;
	
	@Column(name = "bio", nullable = true, columnDefinition = "TEXT")
	private String bio;

	public Long getDirectorID() {
		return directorID;
	}

	public void setDirectorID(Long directorID) {
		this.directorID = directorID;
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

package com.project.ChauPhim.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Actor")
public class Actor {
	@Id
	@Column(name = "actorID", nullable = false)
	private Long actorID;
	
	@Column(name = "rank", nullable = true)
	private int rank;
	
	@Column(name = "bio", nullable = true, columnDefinition = "TEXT")
	private String bio;

	public Long getActorID() {
		return actorID;
	}

	public void setActorID(Long actorID) {
		this.actorID = actorID;
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

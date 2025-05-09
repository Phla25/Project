package com.project.ChauPhim.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Admin")
public class Admin {
	@Id

	@Column(name = "adminID", nullable = false)
	private Long adminID;
	
	public void setAdminID(Long adminID) {
		this.adminID = adminID;
	}
	
	public Long getAdminID() {
		return this.adminID;
	}
}

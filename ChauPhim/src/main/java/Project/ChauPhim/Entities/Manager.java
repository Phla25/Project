package Project.ChauPhim.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Manager", 
		uniqueConstraints = @UniqueConstraint(name = "Manager_username_key", columnNames = "username"))
public class Manager {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "managerID", nullable = false)
	private Long managerID;
	
	@Column(name = "username", length = 256, nullable = false)
	private String username;
	
	@Column(name = "email", length = 256, nullable = false)
	private String email;
	
	@Column(name = "password", length = 256, nullable = false)
	private String password;

	public Long getManagerID() {
		return managerID;
	}

	public void setManagerID(Long managerID) {
		this.managerID = managerID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

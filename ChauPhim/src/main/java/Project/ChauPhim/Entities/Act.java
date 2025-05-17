
package Project.ChauPhim.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Act\"")
public class Act {
	@Id
	@Column(name = "\"actorID\"", nullable = false)
	private Long actorID;
	
	@Id
	@Column(name = "\"movieID\"", nullable = false)
	private Long movieID;
	
	@Column(name = "role", length = 128, nullable = false)
	private String role;

	public Long getActorID() {
		return actorID;
	}

	public void setActorID(Long actorID) {
		this.actorID = actorID;
	}

	public Long getMovieID() {
		return movieID;
	}

	public void setMovieID(Long movieID) {
		this.movieID = movieID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}

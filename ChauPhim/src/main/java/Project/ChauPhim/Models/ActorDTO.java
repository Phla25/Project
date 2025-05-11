package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActorDTO extends Person{
	private List<MovieDTO> moviesPlayed = new ArrayList<MovieDTO>();
	public ActorDTO(String name, String imageURL) {
		super(name, imageURL);
	}
	
	public ActorDTO(String name, LocalDate dob, String gender, String nationality, String imageURL, String bio) {
		super(name, dob, gender, nationality, imageURL, bio);
		// TODO Auto-generated constructor stub
	}

	public List<MovieDTO> getMoviesPlayed() {
		return moviesPlayed;
	}
	public void setMoviesPlayed(List<MovieDTO> moviesPlayed) {
		this.moviesPlayed = moviesPlayed;
	}
	
}

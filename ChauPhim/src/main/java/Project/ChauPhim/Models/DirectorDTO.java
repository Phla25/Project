package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DirectorDTO extends Person{
	private List<MovieDTO> moviesDirected = new ArrayList<MovieDTO>();
	public DirectorDTO(String name, String imageURL) {
		super(name, imageURL);
	}
	
	public DirectorDTO(String name, LocalDate dob, String gender, String nationality, String imageURL, String bio) {
		super(name, dob, gender, nationality, imageURL, bio);
		// TODO Auto-generated constructor stub
	}

	public List<MovieDTO> getMoviesDirected() {
		return moviesDirected;
	}
	public void setMoviesDirected(List<MovieDTO> moviesDirected) {
		this.moviesDirected = moviesDirected;
	}

}

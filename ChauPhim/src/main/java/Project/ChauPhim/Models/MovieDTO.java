package Project.ChauPhim.Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Project.ChauPhim.Entities.Actor;

public class MovieDTO {
	private Long movieID;
	private String title;
	private String posterImageURL;
	private LocalDate releaseDate;
	private String genre;
	private double price;
	private StudioDTO studio;
	private List<Actor> actors = new ArrayList<Actor>();
	public MovieDTO(String title, String posterImageURL) {
		this.title = title;
		this.posterImageURL = posterImageURL;
	}
	// CÁC TRƯỜNG KHÔNG BẮT BUỘC NHẬP MÀ KHÔNG CÓ DỮ LIỆU THÌ MẶC ĐỊNH LÀ NULL
	public MovieDTO(String title, String posterImageURL, LocalDate releaseDate, String genre, double price,
			StudioDTO studio) {
		super();
		this.title = title;		// BẮT BUỘC LÀ NOT NULL
		this.posterImageURL = posterImageURL;	// BẮT BUỘC LÀ NOT NULL
		this.releaseDate = releaseDate;
		this.genre = genre;
		this.price = price;
		this.setStudio(studio);
	}

	public Long getMovieID() {
		return movieID;
	}
	public void setMovieID(Long movieID) {
		this.movieID = movieID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPosterImageURL() {
		return posterImageURL;
	}
	public void setPosterImageURL(String posterImageURL) {
		this.posterImageURL = posterImageURL;
	}
	public LocalDate getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public List<Actor> getActors() {
		return actors;
	}
	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}
	public StudioDTO getStudio() {
		return studio;
	}
	public void setStudio(StudioDTO studio) {
		this.studio = studio;
	}
}

package Project.ChauPhim.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "\"Movie\"",
        uniqueConstraints = @UniqueConstraint(name = "Movie_posterImageURL_key", columnNames = "posterImageURL"))
public class Movie {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE
    )
    @Column(name = "\"movieID\"", nullable = false)
    private Long movieID;
    
    @Column(name = "title", length = 128, nullable = false)
    private String title;
    
    @Column(name = "\"posterImageURL\"", length = 256, nullable = false)
    private String posterImageURL;
    
    @Column(name = "\"releaseDate\"", nullable = false)
    private LocalDate releaseDate;
    
    @Column(name = "genre", length = 32, nullable = true)
    private String genre;
    
    @Column(name = "price", nullable = true)
    private double price;
    
    @Column(name = "\"directorID\"", nullable = true)
    private Long directorID;
    
    @Column(name = "\"studioID\"", nullable = true)
    private Long studioID;
    
    @Column(name = "\"discountID\"", nullable = true)
    private Long discountID;
    
    // Add relationship to Act entities

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Act> acts = new ArrayList<>();

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
    
    public Long getDirectorID() {
        return directorID;
    }
    
    public void setDirectorID(Long directorID) {
        this.directorID = directorID;
    }
    
    public Long getStudioID() {
        return studioID;
    }
    
    public void setStudioID(Long studioID) {
        this.studioID = studioID;
    }
    
    public Long getDiscountID() {
        return discountID;
    }
    
    public void setDiscountID(Long discountID) {
        this.discountID = discountID;
    }

	public List<Act> getActs() {
		return acts;
	}

	public void setActs(List<Act> acts) {
		this.acts = acts;
	}
    


}
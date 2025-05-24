package Project.ChauPhim.Entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Define the composite key class first
class ActId implements Serializable {
    private Long actorID;
    private Long movieID;
    
    // Default constructor required by JPA
    public ActId() {
    }
    
    public ActId(Long actorID, Long movieID) {
        this.actorID = actorID;
        this.movieID = movieID;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActId actId = (ActId) o;
        return Objects.equals(actorID, actId.actorID) && 
               Objects.equals(movieID, actId.movieID);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(actorID, movieID);
    }
}

@Entity
@Table(name = "\"Act\"")
@IdClass(ActId.class)
public class Act {
    @Id
    @Column(name = "\"actorID\"", nullable = false)
    private Long actorID;
    
    @Id
    @Column(name = "\"movieID\"", nullable = false)
    private Long movieID;
    
    @Column(name = "role", length = 128, nullable = false)
    private String role;
    
    // Optional: Add entity references for better navigation
    @ManyToOne
    @JoinColumn(name = "\"actorID\"", insertable = false, updatable = false)
    private Actor actor;
    
    @ManyToOne
    @JoinColumn(name = "\"movieID\"", insertable = false, updatable = false)
    private Movie movie;
    
    // Getters and setters
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
    
    public Actor getActor() {
        return actor;
    }
    
    public void setActor(Actor actor) {
        this.actor = actor;
    }
    
    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
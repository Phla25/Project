package Project.ChauPhim.DAOs;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Act;
import Project.ChauPhim.Entities.Actor;
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
@Repository
public interface ActRepository extends JpaRepository<Act, ActId> {

    // Truy vấn tất cả các Act theo movieID
    List<Act> findByMovieID(Long movieID);

    // Truy vấn xem một Actor đã đóng trong Movie này chưa
    boolean existsByActorIDAndMovieID(Long actorID, Long movieID);

    // Truy vấn tất cả các Actor đóng trong một Movie (join với Actor để lấy thông tin)
    @Query("SELECT a.actor FROM Act a WHERE a.movieID = :movieID")
    List<Actor> findActorsByMovieID(@Param("movieID") Long movieID);
}
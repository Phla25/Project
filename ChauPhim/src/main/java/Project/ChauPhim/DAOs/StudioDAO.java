package Project.ChauPhim.DAOs;

import java.util.List;

import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Entities.Studio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class StudioDAO {
    @PersistenceContext
    private EntityManager entityManager;
    
    public Studio findById(Long studioId) {
        String sql = "SELECT * FROM \"Studio\" WHERE \"studioID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Studio.class);
            query.setParameter(1, studioId);
            return (Studio) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Studio> findAllStudios() {
        String sql = "SELECT * FROM \"Studio\" ORDER BY \"name\"";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByStudioId(Long studioId) {
        String sql = "SELECT m.* FROM \"Movie\" m " +
                     "WHERE m.\"studioID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, studioId);
        return query.getResultList();
    }
}

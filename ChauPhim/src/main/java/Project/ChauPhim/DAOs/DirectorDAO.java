package Project.ChauPhim.DAOs;
import java.util.List;

import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Director;
import Project.ChauPhim.Entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class DirectorDAO {
    @PersistenceContext
    private EntityManager entityManager;
    
    public Director findById(Long directorId) {
        String sql = "SELECT * FROM \"Director\" WHERE \"directorID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Director.class);
            query.setParameter(1, directorId);
            return (Director) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Director> findAllDirectors() {
        String sql = "SELECT * FROM \"Director\" ORDER BY \"name\"";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Director> findPopularDirectors(int limit) {
        String sql = "SELECT d.* FROM \"Director\" d " +
                     "ORDER BY d.\"rank\" DESC NULLS LAST";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByDirectorId(Long directorId) {
        String sql = "SELECT m.* FROM \"Movie\" m " +
                     "WHERE m.\"directorID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, directorId);
        return query.getResultList();
    }
}

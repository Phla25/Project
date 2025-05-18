package Project.ChauPhim.DAOs;

import java.util.List;

import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Repository
public class MovieDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Movie> findLatestMovie(int limit) {
        String sql = "SELECT * FROM \"Movie\" ORDER BY \"releaseDate\" DESC"; 
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findFeaturedMovies(int limit) {
        // co the thay doi logic, lay cac phim noi bat theo tieu chi cua rieng minh
        String sql = "SELECT * FROM \"Movie\" WHERE \"discountID\" IS NOT NULL ORDER BY \"releaseDate\" DESC";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public Movie findById(Long movieId) {
        String sql = "SELECT * FROM \"Movie\" WHERE \"movieID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Movie.class);
            query.setParameter(1, movieId);
            return (Movie) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Movie> findAllMovies() {
        String sql = "SELECT * FROM \"Movie\" ORDER BY \"releaseDate\" DESC";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        return query.getResultList();
    }
}

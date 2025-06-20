package Project.ChauPhim.DAOs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Project.ChauPhim.Entities.Act;
import Project.ChauPhim.Entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class ActDAO {
    @Autowired
    private EntityManager entityManager;
    
    @OneToMany(mappedBy = "actor") // hoặc tương tự
    @JsonIgnore  // Thêm dòng này
    private List<Act> acts;
    
    // hoặc nếu có relationship khác
    @ManyToMany
    @JsonIgnore  // Thêm dòng này  
    private List<Movie> movies;

    /**
     * Add a new actor-movie relationship with role information
     */
    @Transactional
    public void addAct(Act act) {
        String sql = "INSERT INTO \"Act\" (\"actorID\", \"movieID\", \"role\") VALUES (?, ?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, act.getActorID())
                    .setParameter(2, act.getMovieID())
                    .setParameter(3, act.getRole())
                    .executeUpdate();
    }
    
    /**
     * Add a new actor-movie relationship with role information using IDs directly
     */
    @Transactional
    public void addAct(Long actorId, Long movieId, String role) {
        String sql = "INSERT INTO \"Act\" (\"actorID\", \"movieID\", \"role\") VALUES (?, ?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, actorId)
                    .setParameter(2, movieId)
                    .setParameter(3, role)
                    .executeUpdate();
    }
    
    /**
     * Find an act relationship by actor ID and movie ID
     */
    public Act findById(Long actorId, Long movieId) {
        String sql = "SELECT * FROM \"Act\" WHERE \"actorID\" = ? AND \"movieID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Act.class);
            query.setParameter(1, actorId);
            query.setParameter(2, movieId);
            return (Act) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find all roles played by an actor
     */
    @SuppressWarnings("unchecked")
    public List<Act> findRolesByActorId(Long actorId) {
        String sql = "SELECT * FROM \"Act\" WHERE \"actorID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Act.class);
        query.setParameter(1, actorId);
        return query.getResultList();
    }
    
    /**
     * Find all actors and their roles in a specific movie
     */
    @SuppressWarnings("unchecked")
    public List<Act> findRolesByMovieId(Long movieId) {
        String sql = "SELECT * FROM \"Act\" WHERE \"movieID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Act.class);
        query.setParameter(1, movieId);
        return query.getResultList();
    }
    
    @Transactional
    public void updateRole(Long actorId, Long movieId, String newRole) {
        String sql = "UPDATE \"Act\" SET role = ? WHERE \"actorID\" = ? AND \"movieID\" = ?";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, newRole)
                    .setParameter(2, actorId)
                    .setParameter(3, movieId)
                    .executeUpdate();
    }
    
    /**
     * Get all actor-movie relationships
     */
    @SuppressWarnings("unchecked")
    public List<Act> findAllActs() {
        String sql = "SELECT * FROM \"Act\"";
        Query query = entityManager.createNativeQuery(sql, Act.class);
        return query.getResultList();
    }
    
    /**
     * Count how many movies an actor has been in
     */
    public Long countMoviesByActor(Long actorId) {
        String sql = "SELECT COUNT(*) FROM \"Act\" WHERE \"actorID\" = ?";
        return (Long) entityManager.createNativeQuery(sql)
                                  .setParameter(1, actorId)
                                  .getSingleResult();
    }
    
    /**
     * Count how many actors are in a movie
     */
    public Long countActorsByMovie(Long movieId) {
        String sql = "SELECT COUNT(*) FROM \"Act\" WHERE \"movieID\" = ?";
        return (Long) entityManager.createNativeQuery(sql)
                                  .setParameter(1, movieId)
                                  .getSingleResult();
    }
}
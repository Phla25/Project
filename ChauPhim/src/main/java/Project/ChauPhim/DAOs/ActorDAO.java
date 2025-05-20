package Project.ChauPhim.DAOs;

import java.util.List;

import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class ActorDAO {
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public void addActor(Actor actor) {
    	String sql = "INSERT INTO \"Actor\" (name, \"imageURL\") VALUES (?, ?)";
		entityManager.createNativeQuery(sql)
					.setParameter(1, actor.getName())
					.setParameter(2, actor.getImageURL())
					.executeUpdate();
    }
    public Actor findById(Long actorId) {
        String sql = "SELECT * FROM \"Actor\" WHERE \"actorID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Actor.class);
            query.setParameter(1, actorId);
            return (Actor) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Actor> findActorsByName(String name){
    	String sql = "SELECT * FROM \"Actor\" WHERE name = ?";
    	Query query = entityManager.createNativeQuery(sql, Actor.class);
    	query.setParameter(1, name);
    	return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Actor> findActorsByMovieId(Long movieId) {
        String sql = "SELECT a.* FROM \"Actor\" a " +
                     "JOIN \"Act\" act ON a.\"actorID\" = act.\"actorID\" " +
                     "WHERE act.\"movieID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setParameter(1, movieId);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Actor> findAllActors() {
        String sql = "SELECT * FROM \"Actor\" ORDER BY \"name\"";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Actor> findPopularActors(int limit) {
        String sql = "SELECT a.* FROM \"Actor\" a " +
                     "ORDER BY a.\"rank\" DESC NULLS LAST";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
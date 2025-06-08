package Project.ChauPhim.DAOs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class ActorDAO {
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Add a new actor with complete details
     */
    @Transactional
    public void addActor(Actor actor) {
        // Use JPA entity manager persist for full entity
        entityManager.persist(actor);
    }
    
    /**
     * Add actor with basic information using native query
     */
    @Transactional
    public void addActorBasic(Actor actor) {
        String sql = "INSERT INTO \"Actor\" (name, \"imageURL\") VALUES (?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, actor.getName())
                    .setParameter(2, actor.getImageURL())
                    .executeUpdate();
    }
    
    /**
     * Find actor by ID
     */
    public Actor findById(Long actorId) {
        try {
            return entityManager.find(Actor.class, actorId);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find actor by ID using native query
     */
    public Actor findByIdNative(Long actorId) {
        String sql = "SELECT * FROM \"Actor\" WHERE \"actorID\" = ?";
        try {
            Query query = entityManager.createNativeQuery(sql, Actor.class);
            query.setParameter(1, actorId);
            return (Actor) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find actors by exact name match
     */
    @SuppressWarnings("unchecked")
    public List<Actor> findActorsByName(String name) {
        String sql = "SELECT * FROM \"Actor\" WHERE name = ?";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setParameter(1, name);
        return query.getResultList();
    }
    
    /**
     * Find actors by partial name match (case insensitive)
     */
    @SuppressWarnings("unchecked")
    public List<Actor> findActorsByPartialName(String partialName) {
        String sql = "SELECT * FROM \"Actor\" WHERE LOWER(name) LIKE LOWER(?)";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setParameter(1, "%" + partialName + "%");
        return query.getResultList();
    }
    
    /**
     * Find actors in a specific movie
     */
    @SuppressWarnings("unchecked")
    public List<Actor> findActorsByMovieId(Long movieId) {
        String sql = "SELECT a.* FROM \"Actor\" a " +
                     "JOIN \"Act\" act ON a.\"actorID\" = act.\"actorID\" " +
                     "WHERE act.\"movieID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setParameter(1, movieId);
        return query.getResultList();
    }
    
    /**
     * Find actors in a specific movie with their roles
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findActorsWithRolesByMovieId(Long movieId) {
        String sql = "SELECT a.*, act.role FROM \"Actor\" a " +
                     "JOIN \"Act\" act ON a.\"actorID\" = act.\"actorID\" " +
                     "WHERE act.\"movieID\" = ? " +
                     "ORDER BY a.name";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, movieId);
        return query.getResultList();
    }
    
    /**
     * Find all actors
     */
    @SuppressWarnings("unchecked")
    public List<Actor> findAllActors() {
        String sql = "SELECT * FROM \"Actor\" ORDER BY name";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        return query.getResultList();
    }
    
    /**
     * Find most popular actors
     */
    @SuppressWarnings("unchecked")
    public List<Actor> findPopularActors(int limit) {
        String sql = "SELECT a.* FROM \"Actor\" a " +
                     "ORDER BY a.\"rank\" DESC NULLS LAST " +
                     "LIMIT ?";
        Query query = entityManager.createNativeQuery(sql, Actor.class);
        query.setParameter(1, limit);
        return query.getResultList();
    }
    
    /**
     * Update actor information
     */
    @Transactional
    public void updateActor(Actor actor) {
        entityManager.merge(actor);
    }
    
    /**
     * Update specific actor information
     */
    @Transactional
    public void updateActorFields(Long actorId, Map<String, Object> fieldsToUpdate) {
        if (fieldsToUpdate == null || fieldsToUpdate.isEmpty()) {
            return;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE \"Actor\" SET ");
        int paramIndex = 1;
        Object[] paramValues = new Object[fieldsToUpdate.size() + 1]; // +1 for actorId
        
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            if (paramIndex > 1) {
                sql.append(", ");
            }
            
            String fieldName = entry.getKey();
            // Ensure proper quoting for column names
            if (fieldName.equals("imageURL") || fieldName.equals("actorID")) {
                sql.append("\"").append(fieldName).append("\"");
            } else {
                sql.append(fieldName);
            }
            
            sql.append(" = ?");
            paramValues[paramIndex - 1] = entry.getValue();
            paramIndex++;
        }
        
        sql.append(" WHERE \"actorID\" = ?");
        paramValues[paramIndex - 1] = actorId;
        
        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < paramValues.length; i++) {
            query.setParameter(i + 1, paramValues[i]);
        }
        
        query.executeUpdate();
    }
    
    /**
     * Count total actors
     */
    public Long countActors() {
        String sql = "SELECT COUNT(\"actorID\") FROM \"Actor\"";
        return (Long) entityManager.createNativeQuery(sql).getSingleResult();
    }
}
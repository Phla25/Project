package Project.ChauPhim.DAOs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class DirectorDAO {
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Add a new director with complete details
     */
    @Transactional
    public void addDirector(Director director) {
        entityManager.persist(director);
    }
    
    /**
     * Add director with basic information using native query
     */
    @Transactional
    public void addDirectorBasic(Director director) {
        String sql = "INSERT INTO \"Director\" (name, \"imageURL\") VALUES (?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, director.getName())
                    .setParameter(2, director.getImageURL())
                    .executeUpdate();
    }
    
    /**
     * Find director by ID
     */
    public Director findById(Long directorId) {
        try {
            return entityManager.find(Director.class, directorId);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find directors by exact name match
     */
    @SuppressWarnings("unchecked")
    public List<Director> findDirectorsByName(String name) {
        String sql = "SELECT * FROM \"Director\" WHERE name = ?";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        query.setParameter(1, name);
        return query.getResultList();
    }
    
    /**
     * Find directors by partial name match (case insensitive)
     */
    @SuppressWarnings("unchecked")
    public List<Director> findDirectorsByPartialName(String partialName) {
        String sql = "SELECT * FROM \"Director\" WHERE LOWER(name) LIKE LOWER(?)";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        query.setParameter(1, "%" + partialName + "%");
        return query.getResultList();
    }
    
    /**
     * Find directors who have directed specific genres
     */
    @SuppressWarnings("unchecked")
    public List<Director> findDirectorsByGenre(String genre) {
        String sql = "SELECT DISTINCT d.* FROM \"Director\" d " +
                     "JOIN \"Movie\" m ON d.\"directorID\" = m.\"directorID\" " +
                     "WHERE m.genre = ?";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        query.setParameter(1, genre);
        return query.getResultList();
    }
    
    /**
     * Find all directors
     */
    @SuppressWarnings("unchecked")
    public List<Director> findAllDirectors() {
        String sql = "SELECT * FROM \"Director\" ORDER BY name";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        return query.getResultList();
    }
    
    /**
     * Find most prolific directors (with most movies)
     */
    @SuppressWarnings("unchecked")
    public List<Director> findTopDirectors(int limit) {
        String sql = "SELECT d.* FROM \"Director\" d " +
                     "JOIN (SELECT \"directorID\", COUNT(*) as movie_count FROM \"Movie\" " +
                     "      GROUP BY \"directorID\" ORDER BY movie_count DESC LIMIT ?) counts " +
                     "ON d.\"directorID\" = counts.\"directorID\" " +
                     "ORDER BY counts.movie_count DESC";
        Query query = entityManager.createNativeQuery(sql, Director.class);
        query.setParameter(1, limit);
        return query.getResultList();
    }
    
    /**
     * Update director information
     */
    @Transactional
    public void updateDirector(Director director) {
        entityManager.merge(director);
    }
    
    /**
     * Update specific director information
     */
    @Transactional
    public void updateDirectorFields(Long directorId, Map<String, Object> fieldsToUpdate) {
        if (fieldsToUpdate == null || fieldsToUpdate.isEmpty()) {
            return;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE \"Director\" SET ");
        int paramIndex = 1;
        Object[] paramValues = new Object[fieldsToUpdate.size() + 1]; // +1 for directorId
        
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            if (paramIndex > 1) {
                sql.append(", ");
            }
            
            String fieldName = entry.getKey();
            // Ensure proper quoting for column names
            if (fieldName.equals("imageURL") || fieldName.equals("directorID")) {
                sql.append("\"").append(fieldName).append("\"");
            } else {
                sql.append(fieldName);
            }
            
            sql.append(" = ?");
            paramValues[paramIndex - 1] = entry.getValue();
            paramIndex++;
        }
        
        sql.append(" WHERE \"directorID\" = ?");
        paramValues[paramIndex - 1] = directorId;
        
        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < paramValues.length; i++) {
            query.setParameter(i + 1, paramValues[i]);
        }
        
        query.executeUpdate();
    }
    
    /**
     * Delete a director
     */
    @Transactional
    public void deleteDirector(Long directorId) {
        // Update movies to set directorID to null
        entityManager.createNativeQuery("UPDATE \"Movie\" SET \"directorID\" = NULL WHERE \"directorID\" = ?")
                    .setParameter(1, directorId)
                    .executeUpdate();
        
        // Then delete the director
        entityManager.createNativeQuery("DELETE FROM \"Director\" WHERE \"directorID\" = ?")
                    .setParameter(1, directorId)
                    .executeUpdate();
    }
    
    /**
     * Count total directors
     */
    public Long countDirectors() {
        String sql = "SELECT COUNT(\"directorID\") FROM \"Director\"";
        return (Long) entityManager.createNativeQuery(sql).getSingleResult();
    }
    
    /**
     * Count movies by director
     */
    public Long countMoviesByDirector(Long directorId) {
        String sql = "SELECT COUNT(*) FROM \"Movie\" WHERE \"directorID\" = ?";
        return (Long) entityManager.createNativeQuery(sql)
                                  .setParameter(1, directorId)
                                  .getSingleResult();
    }
}
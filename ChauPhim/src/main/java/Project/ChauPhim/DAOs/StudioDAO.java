package Project.ChauPhim.DAOs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import Project.ChauPhim.Entities.Studio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class StudioDAO {
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Add a new studio with complete details
     */
    @Transactional
    public void addStudio(Studio studio) {
        entityManager.persist(studio);
    }
    
    /**
     * Add studio with basic information using native query
     */
    @Transactional
    public void addStudioBasic(Studio studio) {
        String sql = "INSERT INTO \"Studio\" (name, country) VALUES (?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, studio.getName())
                    .setParameter(2, studio.getCountry())
                    .executeUpdate();
    }
    
    /**
     * Find studio by ID
     */
    public Studio findById(Long studioId) {
        try {
            return entityManager.find(Studio.class, studioId);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find studios by exact name match
     */
    @SuppressWarnings("unchecked")
    public List<Studio> findStudiosByName(String name) {
        String sql = "SELECT * FROM \"Studio\" WHERE name = ?";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        query.setParameter(1, name);
        return query.getResultList();
    }
    
    /**
     * Find studios by partial name match (case insensitive)
     */
    @SuppressWarnings("unchecked")
    public List<Studio> findStudiosByPartialName(String partialName) {
        String sql = "SELECT * FROM \"Studio\" WHERE LOWER(name) LIKE LOWER(?)";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        query.setParameter(1, "%" + partialName + "%");
        return query.getResultList();
    }
    
    /**
     * Find studios by country
     */
    @SuppressWarnings("unchecked")
    public List<Studio> findStudiosByCountry(String country) {
        String sql = "SELECT * FROM \"Studio\" WHERE country = ?";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        query.setParameter(1, country);
        return query.getResultList();
    }
    
    /**
     * Find all studios
     */
    @SuppressWarnings("unchecked")
    public List<Studio> findAllStudios() {
        String sql = "SELECT * FROM \"Studio\" ORDER BY name";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        return query.getResultList();
    }
    
    /**
     * Find studios with most movies
     */
    @SuppressWarnings("unchecked")
    public List<Studio> findMostProductiveStudios(int limit) {
        String sql = "SELECT s.* FROM \"Studio\" s " +
                     "LEFT JOIN (SELECT \"studioID\", COUNT(*) as movie_count FROM \"Movie\" " +
                     "           GROUP BY \"studioID\") counts " +
                     "ON s.\"studioID\" = counts.\"studioID\" " +
                     "ORDER BY counts.movie_count DESC NULLS LAST " +
                     "LIMIT ?";
        Query query = entityManager.createNativeQuery(sql, Studio.class);
        query.setParameter(1, limit);
        return query.getResultList();
    }
    
    /**
     * Update studio information
     */
    @Transactional
    public void updateStudio(Studio studio) {
        entityManager.merge(studio);
    }
    
    /**
     * Update specific studio information
     */
    @Transactional
    public void updateStudioFields(Long studioId, Map<String, Object> fieldsToUpdate) {
        if (fieldsToUpdate == null || fieldsToUpdate.isEmpty()) {
            return;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE \"Studio\" SET ");
        int paramIndex = 1;
        Object[] paramValues = new Object[fieldsToUpdate.size() + 1]; // +1 for studioId
        
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            if (paramIndex > 1) {
                sql.append(", ");
            }
            
            String fieldName = entry.getKey();
            // Ensure proper quoting for column names
            if (fieldName.equals("logoURL") || fieldName.equals("studioID")) {
                sql.append("\"").append(fieldName).append("\"");
            } else {
                sql.append(fieldName);
            }
            
            sql.append(" = ?");
            paramValues[paramIndex - 1] = entry.getValue();
            paramIndex++;
        }
        
        sql.append(" WHERE \"studioID\" = ?");
        paramValues[paramIndex - 1] = studioId;
        
        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < paramValues.length; i++) {
            query.setParameter(i + 1, paramValues[i]);
        }
        
        query.executeUpdate();
    }
    
    /**
     * Delete a studio
     */
    @Transactional
    public void deleteStudio(Long studioId) {
        // Update movies to set studioID to null
        entityManager.createNativeQuery("UPDATE \"Movie\" SET \"studioID\" = NULL WHERE \"studioID\" = ?")
                    .setParameter(1, studioId)
                    .executeUpdate();
        
        // Then delete the studio
        entityManager.createNativeQuery("DELETE FROM \"Studio\" WHERE \"studioID\" = ?")
                    .setParameter(1, studioId)
                    .executeUpdate();
    }
    
    /**
     * Count total studios
     */
    public Long countStudios() {
        String sql = "SELECT COUNT(\"studioID\") FROM \"Studio\"";
        return (Long) entityManager.createNativeQuery(sql).getSingleResult();
    }
    
    /**
     * Count movies by studio
     */
    public Long countMoviesByStudio(Long studioId) {
        String sql = "SELECT COUNT(*) FROM \"Movie\" WHERE \"studioID\" = ?";
        return (Long) entityManager.createNativeQuery(sql)
                                  .setParameter(1, studioId)
                                  .getSingleResult();
    }
}
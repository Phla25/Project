package Project.ChauPhim.DAOs;

import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Entities.Studio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class MovieDAO {
    @Autowired
    private EntityManager entityManager;
   
    @Autowired
    private ActRepository actRepository;

    public List<Movie> findAllMovies() {
        List<Movie> movies = entityManager.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
        // Load actors for each movie
        for (Movie movie : movies) {
            List<Actor> actors = actRepository.findActorsByMovieID(movie.getMovieID());
            movie.getActs().forEach(act -> act.setActor(actors.stream()
                    .filter(actor -> actor.getActorID().equals(act.getActorID()))
                    .findFirst()
                    .orElse(null)));
        }
        return movies;
    }

    public Movie findById(Long movieID) {
        Movie movie = entityManager.find(Movie.class, movieID);
        if (movie != null) {
            List<Actor> actors = actRepository.findActorsByMovieID(movieID);
            movie.getActs().forEach(act -> act.setActor(actors.stream()
                    .filter(actor -> actor.getActorID().equals(act.getActorID()))
                    .findFirst()
                    .orElse(null)));
        }
        return movie;
    }

    /**
     * Add a new movie with complete details
     */
    @Transactional
    public void addMovie(Movie movie) {
        entityManager.persist(movie);
    }
    
    /**
     * Add a new movie with basic information
     */
    @Transactional
    public void addMovieBasic(Movie movie) {
        String sql = "INSERT INTO \"Movie\" (title, \"posterImageURL\", \"releaseDate\") VALUES (?, ?, ?)";
        entityManager.createNativeQuery(sql)
                    .setParameter(1, movie.getTitle())
                    .setParameter(2, movie.getPosterImageURL())
                    .setParameter(3, movie.getReleaseDate())
                    .executeUpdate();
    }
    

    
    /**
     * Find movies by actor name
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByActor(String actorName) {
        String sql = "SELECT m.* FROM \"Movie\" m " +
                     "JOIN \"Act\" a ON m.\"movieID\" = a.\"movieID\" " +
                     "JOIN \"Actor\" act ON a.\"actorID\" = act.\"actorID\" " +
                     "WHERE act.name = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, actorName);
        return query.getResultList();
    }
    
    /**
     * Find movies by actor ID
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByActorId(Long actorId) {
        String sql = "SELECT m.* FROM \"Movie\" m " +
                     "JOIN \"Act\" a ON m.\"movieID\" = a.\"movieID\" " +
                     "WHERE a.\"actorID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, actorId);
        return query.getResultList();
    }
    
    /**
     * Find movies by exact title match
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByTitle(String title) {
        String sql = "SELECT * FROM \"Movie\" WHERE LOWER(title) LIKE ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, title.toLowerCase());

        return query.getResultList();
    }
    
    /**
     * Find movies by partial title match (case insensitive)
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByPartialTitle(String partialTitle) {
        String sql = "SELECT * FROM \"Movie\" WHERE LOWER(title) LIKE LOWER(?)";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, "%" + partialTitle + "%");
        return query.getResultList();
    }
    
    /**
     * Find movies by genre/category
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByGenre(String genre) {
        String sql = "SELECT * FROM \"Movie\" WHERE genre = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, genre);
        return query.getResultList();
    }
    
    /**
     * Find movies released between given dates
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM \"Movie\" WHERE \"releaseDate\" BETWEEN ? AND ? ORDER BY \"releaseDate\" DESC";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        return query.getResultList();
    }
    
    /**

     * Find all movies with pagination
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findAllMoviesPaginated(int pageNumber, int pageSize) {
        String sql = "SELECT * FROM \"Movie\" ORDER BY \"releaseDate\" DESC";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    /**
     * Find latest released movies
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findLatestMovies(int limit) {
        String sql = "SELECT * FROM \"Movie\" ORDER BY \"releaseDate\" DESC LIMIT ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, limit);
        return query.getResultList();
    }
    
    /**
     * Update movie information
     */
    @Transactional
    public void updateMovie(Long movieID, String title, String posterImageURL, 
                              LocalDate releaseDate,Double price, String genre, Long discountID, Long directorID, Long studioID) {
        
        // Tạo câu lệnh SQL động dựa trên các trường không null
        StringBuilder sqlBuilder = new StringBuilder("UPDATE \"Movie\" SET ");
        
        // Danh sách tham số và giá trị sẽ được sử dụng
        List<String> updateFields = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        
        // Kiểm tra và thêm các trường cần cập nhật
        if (title != null && !title.trim().isEmpty()) {
            updateFields.add("\"title\" = ?");
            paramValues.add(title);
        }
        
        if (posterImageURL != null && !posterImageURL.trim().isEmpty()) {
            updateFields.add("\"posterImageURL\" = ?");
            paramValues.add(posterImageURL);
        }
        
        if (releaseDate != null) {
            updateFields.add("\"releaseDate\" = ?");
            paramValues.add(releaseDate);
        }
        
        if (releaseDate != null) {
            updateFields.add("\"price\" = ?");
            paramValues.add(price);
        }
        
        if (genre != null && !genre.trim().isEmpty()) {
            updateFields.add("\"genre\" = ?");
            paramValues.add(genre);
        }
        
        if (discountID != null) {
            updateFields.add("\"discountID\" = ?");
            paramValues.add(discountID);
        }
        
        if (directorID != null) {
            updateFields.add("\"directorID\" = ?");
            paramValues.add(directorID);
        }
        
        if (studioID != null) {
            updateFields.add("\"studioID\" = ?");
            paramValues.add(studioID);
        }
        
        // Nếu không có trường nào cần cập nhật, trả về
        if (updateFields.isEmpty()) {
            return;
        }
        
        // Hoàn thành câu lệnh SQL
        sqlBuilder.append(String.join(", ", updateFields));
        sqlBuilder.append(" WHERE \"movieID\" = ?");
        paramValues.add(movieID);
        
        // Tạo câu truy vấn và thiết lập tham số
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        
        // Thiết lập các tham số
        for (int i = 0; i < paramValues.size(); i++) {
            query.setParameter(i + 1, paramValues.get(i));
        }
        
        // Thực thi câu truy vấn
        query.executeUpdate();
    }

    /**
     * Update specific movie information
     */
    @Transactional
    public void updateMovieFields(Long movieId, Map<String, Object> fieldsToUpdate) {
        if (fieldsToUpdate == null || fieldsToUpdate.isEmpty()) {
            return;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE \"Movie\" SET ");
        int paramIndex = 1;
        Object[] paramValues = new Object[fieldsToUpdate.size() + 1]; // +1 for movieId
        
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            if (paramIndex > 1) {
                sql.append(", ");
            }
            
            String fieldName = entry.getKey();
            // Ensure proper quoting for column names with special characters
            if (fieldName.contains("ID") || fieldName.equals("posterImageURL") || fieldName.equals("releaseDate")) {
                sql.append("\"").append(fieldName).append("\"");
            } else {
                sql.append(fieldName);
            }
            
            sql.append(" = ?");
            paramValues[paramIndex - 1] = entry.getValue();
            paramIndex++;
        }
        
        sql.append(" WHERE \"movieID\" = ?");
        paramValues[paramIndex - 1] = movieId;
        
        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < paramValues.length; i++) {
            query.setParameter(i + 1, paramValues[i]);
        }
        
        query.executeUpdate();
    }
    
    /**
     * Delete a movie
     */
    @Transactional
    public void deleteMovie(Long movieId) {
        // First delete from Act table to maintain referential integrity
        entityManager.createNativeQuery("DELETE FROM \"Act\" WHERE \"movieID\" = ?")
                    .setParameter(1, movieId)
                    .executeUpdate();
        
        // Then delete the movie
        entityManager.createNativeQuery("DELETE FROM \"Movie\" WHERE \"movieID\" = ?")
                    .setParameter(1, movieId)
                    .executeUpdate();
    }
    
    /**
     * Count total movies
     */
    public Long countMovies() {
        String sql = "SELECT COUNT(\"movieID\") FROM \"Movie\"";
        return (Long) entityManager.createNativeQuery(sql).getSingleResult();
    }
    
    /**
     * Get studio information for a movie
     */
    public Studio findStudioByMovie(Long movieId) {
        try {
            String sql = "SELECT s.* FROM \"Studio\" s " +
                         "JOIN \"Movie\" m ON s.\"studioID\" = m.\"studioID\" " +
                         "WHERE m.\"movieID\" = ?";
            Query query = entityManager.createNativeQuery(sql, Studio.class);
            query.setParameter(1, movieId);
            return (Studio) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find movies by studio ID
     */
    @SuppressWarnings("unchecked")
    public List<Movie> findMoviesByStudioId(Long studioId) {
        String sql = "SELECT * FROM \"Movie\" WHERE \"studioID\" = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, studioId);
        return query.getResultList();
    }

	@SuppressWarnings("unchecked")
	public List<Movie> findMovieByCategory(String genre) {
		String sql = "SELECT * FROM \"Movie\" WHERE \"genre\" = ?";
        Query query = entityManager.createNativeQuery(sql, Movie.class);
        query.setParameter(1, genre);
        return query.getResultList();
		
	}

	@SuppressWarnings("unchecked")
	 public List<Movie> findLatestMovie(int limit) {
	        String sql = "SELECT * FROM \"Movie\" ORDER BY \"releaseDate\" DESC"; 
	        Query query = entityManager.createNativeQuery(sql, Movie.class);
	        query.setMaxResults(limit);
	        return query.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDirectorName(Long directorID) {
		String sql = "SELECT name FROM \"Movie\" JOIN \"Director\" USING (\"directorID\") WHERE \"directorID\" = ?";
		Query query = entityManager.createNativeQuery(sql).setParameter(1, directorID);
		return query.getResultList();
	}
    
	@SuppressWarnings("unchecked")
	public List<String> getStudioName(Long studioID) {
		String sql = "SELECT name FROM \"Movie\" JOIN \"Studio\" USING (\"studioID\") WHERE \"studioID\" = ?";
		Query query = entityManager.createNativeQuery(sql).setParameter(1, studioID);
		return query.getResultList();
	}

}
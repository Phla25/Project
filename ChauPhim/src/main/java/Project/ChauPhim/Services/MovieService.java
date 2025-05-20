package Project.ChauPhim.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Project.ChauPhim.DAOs.ActorDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Models.MovieDTO;
import Project.ChauPhim.Models.StudioDTO;

@Service
public class MovieService {
    
    @Autowired
    private MovieDAO movieDAO;
    
    @Autowired
    private ActorDAO actorDAO;
    
    // Method to convert Movie entity to MovieDTO and populate actors
    public MovieDTO convertToDTO(Movie movie) {
        // Create a new MovieDTO with required fields
        MovieDTO dto = new MovieDTO(
            movie.getTitle(),
            movie.getPosterImageURL(),
            movie.getReleaseDate(),
            movie.getGenre(),
            movie.getPrice(),
            new StudioDTO(movieDAO.showStudioInfo(movie).getName(),
            		movieDAO.showStudioInfo(movie).getYear(),
            		movieDAO.showStudioInfo(movie).getCountry()
            		) // This would need a proper implementation
        );
        
        // Fetch and set actors for this movie
        List<Actor> actors = actorDAO.findActorsByMovieId(movie.getMovieID());
        dto.setActors(actors);
        
        return dto;
    }
    
    // Method to convert multiple Movie entities to DTOs
    public List<MovieDTO> convertToDTOList(List<Movie> movies) {
        List<MovieDTO> dtoList = new ArrayList<>();
        
        for (Movie movie : movies) {
            dtoList.add(convertToDTO(movie));
        }
        
        return dtoList;
    }
    
    // Method to get all movies as DTOs
    public List<MovieDTO> getAllMoviesWithDetails() {
        List<Movie> movies = movieDAO.findAllMovies();
        return convertToDTOList(movies);
    }
    
    // Method to search movies by various criteria and return as DTOs
    public List<MovieDTO> searchMovies(String searchType, String searchTerm) {
        List<Movie> movies;
        
        if (searchType != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
            switch (searchType) {
                case "title":
                    movies = movieDAO.findMovieByTitle(searchTerm);
                    break;
                case "actor":
                    movies = movieDAO.findMovieByActor(searchTerm);
                    break;
                case "category":
                    movies = movieDAO.findMovieByCategory(searchTerm);
                    break;
                default:
                    movies = movieDAO.findAllMovies();
            }
        } else {
            movies = movieDAO.findAllMovies();
        }
        
        return convertToDTOList(movies);
    }
}
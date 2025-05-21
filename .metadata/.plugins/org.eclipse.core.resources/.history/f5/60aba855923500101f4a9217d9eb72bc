package Project.ChauPhim.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Project.ChauPhim.DAOs.ActorDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Movie;

@Service
public class CustomerService implements AppUserService{
	@Autowired
	private MovieDAO movieDAO;
	@Autowired
	private ActorDAO actorDAO;
	
	@Override
	public List<Movie> findMovieByActor(String name) {
		movieDAO.findMovieByActor(name);
		return null;
	}

	@Override
	public List<Movie> findMovieByTitle(String title) {
		movieDAO.findMovieByTitle(title);
		return null;
	}

	@Override
	public List<Movie> findMovieByCategory(String genre) {
		movieDAO.findMovieByCategory(genre);
		return null;
	}

	@Override
	public List<Movie> findAllMovies() {
		movieDAO.findAllMovies();
		return null;
	}

	@Override
	public Actor findById(Long actorId) {
		actorDAO.findById(actorId);
		return null;
	}

	@Override
	public List<Actor> findActorsByName(String name) {
		actorDAO.findActorsByName(name);
		return null;
	}

	@Override
	public List<Actor> findActorsByMovieId(Long movieId) {
		actorDAO.findActorsByMovieId(movieId);
		return null;
	}

	@Override
	public List<Actor> findAllActors() {
		actorDAO.findAllActors();
		return null;
	}

	@Override
	public List<Actor> findPopularActors(int limit) {
		actorDAO.findPopularActors(limit);
		return null;
	}

}

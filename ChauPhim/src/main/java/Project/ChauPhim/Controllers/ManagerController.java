package Project.ChauPhim.Controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Project.ChauPhim.DAOs.ActRepository;
import Project.ChauPhim.DAOs.ActorDAO;
import Project.ChauPhim.DAOs.DirectorDAO;
import Project.ChauPhim.DAOs.ManagerDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.DAOs.StudioDAO;
import Project.ChauPhim.Entities.Act;
import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Director;
import Project.ChauPhim.Entities.Manager;
import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Entities.Studio;
import Project.ChauPhim.Models.MovieDTO;
import Project.ChauPhim.Services.ManagerDashboardService;

@Controller
public class ManagerController {
    @Autowired
    private MovieDAO movieDAO;
    @Autowired
    private ManagerDAO managerDAO;
    @Autowired
    private ActorDAO actorDAO;
    @Autowired
    private StudioDAO studioDAO;
    @Autowired
    private DirectorDAO directorDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ManagerDashboardService dashboardService;
    @Autowired
    private ActRepository actRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    
    @GetMapping("/manager-register")
    public String showSignInPage(Model model) {
        model.addAttribute("manager", new Manager());
        return "manager-register";
    }

    @PostMapping("/manager-register")
    @Transactional
    public String processRegisterManager(@ModelAttribute("manager") Manager manager, Model model) {
        try {
            Manager existingManager = managerDAO.findByUserName(manager.getUsername());
            if (existingManager != null) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                return "manager-register";
            }
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            managerDAO.addManager(manager);
            return "redirect:/manager-login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "manager-register";
        }
    }

    @GetMapping("/manager-login")
    public String showLoginPage() {
        return "manager-login";
    }

    @PostMapping("/manager-login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            Manager manager = managerDAO.findByUserName(username);
            if (manager != null && passwordEncoder.matches(password, manager.getPassword())) {
                return "redirect:/manager/dashboard";
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu sai!");
                return "manager-login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "manager-login";
        }
    }

    @GetMapping("/manager/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Manager manager = managerDAO.findByUserName(username);
            model.addAttribute("manager", manager);
            return "manager-profile";
        }
        return "redirect:/manager-login";
    }

    @GetMapping("/manager/dashboard")
    public String viewDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/manager-login";
        }
        String username = principal.getName();
        Manager manager = managerDAO.findByUserName(username);
        model.addAttribute("manager", manager);
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        Long userCount = dashboardService.getUserCount();
        Long discountCount = dashboardService.getDiscountCount();
        Long todaySales = dashboardService.getTodaySales();
        Long monthlySales = dashboardService.getMonthlySales(currentMonth);
        Long yearlySales = dashboardService.getYearlySales(currentYear);
        Map<String, Long> monthlySalesData = new HashMap<>();
        for (int i = 5; i >= 0; i--) {
            int month = currentMonth - i;
            int year = currentYear;
            if (month <= 0) {
                month += 12;
                year -= 1;
            }
            String monthName = getMonthName(month);
            Long sales = dashboardService.getMonthlySales(month);
            monthlySalesData.put(monthName, sales);
        }
        model.addAttribute("userCount", userCount);
        model.addAttribute("discountCount", discountCount);
        model.addAttribute("todaySales", todaySales);
        model.addAttribute("monthlySales", monthlySales);
        model.addAttribute("yearlySales", yearlySales);
        model.addAttribute("monthlySalesData", monthlySalesData);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);
        return "manager-dashboard";
    }

    @PostMapping("/manager/update")
    @PreAuthorize("hasRole('MANAGER')")
    public String updateManagerInfo(
            @ModelAttribute("manager") Manager formManager,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                managerDAO.updateManager(username, formManager.getEmail());
                redirectAttributes.addFlashAttribute("successMessage", "Thông tin đã được cập nhật thành công!");
                return "redirect:/manager/profile";
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                Manager manager = managerDAO.findByUserName(username);
                model.addAttribute("manager", manager);
                return "manager-profile";
            }
        }
        return "redirect:/manager-login";
    }

    @GetMapping("/api/sales-data")
    @ResponseBody
    public Map<String, Object> getSalesData() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        Map<String, Object> data = new HashMap<>();
        Map<String, Long> monthlySalesData = new HashMap<>();
        for (int i = 5; i >= 0; i--) {
            int month = currentMonth - i;
            int year = currentYear;
            if (month <= 0) {
                month += 12;
                year -= 1;
            }
            String monthName = getMonthName(month);
            Long sales = dashboardService.getMonthlySales(month);
            monthlySalesData.put(monthName, sales);
        }
        data.put("monthlySales", monthlySalesData);
        return data;
    }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }

    @GetMapping("/manager/movies")
    @PreAuthorize("hasRole('MANAGER')")
    public String showMoviePage(Model model) {
        List<Movie> movies = movieDAO.findAllMovies();
        model.addAttribute("movies", movies);
        return "manager-movies";
    }

    @PostMapping("/manager/movies")
    @ResponseBody
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> addMovieToStore(
            @RequestParam("title") String title,
            @RequestParam("posterImageURL") String posterImageURL,
            @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDate,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "discountID", required = false) Long discountID,
            @RequestParam(value = "directorID", required = false) Long directorID,
            @RequestParam(value = "studioID", required = false) Long studioId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Movie title is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (releaseDate == null) {
                response.put("success", false);
                response.put("message", "Release date is required");
                return ResponseEntity.badRequest().body(response);
            }
            Movie movie = new Movie();
            movie.setTitle(title.trim());
            movie.setPosterImageURL(posterImageURL != null ? posterImageURL.trim() : "");
            movie.setReleaseDate(releaseDate);
            if (genre != null && !genre.trim().isEmpty()) {
                movie.setGenre(genre.trim());
            }
            if (price != null && price > 0) {
                movie.setPrice(price);
            }
            if (discountID != null) {
                movie.setDiscountID(discountID);
            }
            if (directorID != null) {
                movie.setDirectorID(directorID);
            }
            if (studioId != null) {
                movie.setStudioID(studioId);
            }
            movieDAO.addMovie(movie);
            response.put("success", true);
            response.put("message", "Movie added successfully");
            response.put("movieId", movie.getMovieID());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding movie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/movies")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getMovies(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "title", required = false) String title) {
        List<Movie> movies;
        if (title != null && !title.trim().isEmpty()) {
            movies = movieDAO.findMoviesByTitle(title.trim());
        } else if (genre != null && !genre.trim().isEmpty()) {
            movies = movieDAO.findMoviesByGenre(genre.trim());
        } else {
            movies = movieDAO.findAllMovies();
        }

        List<Map<String, Object>> movieMaps = movies.stream().map(movie -> {
            Map<String, Object> movieMap = new HashMap<>();
            movieMap.put("movieID", movie.getMovieID());
            movieMap.put("title", movie.getTitle());
            movieMap.put("posterImageURL", movie.getPosterImageURL());
            movieMap.put("releaseDate", movie.getReleaseDate());
            movieMap.put("genre", movie.getGenre());
            movieMap.put("price", movie.getPrice());
            movieMap.put("studioID", movie.getStudioID());
            movieMap.put("directorID", movie.getDirectorID());
            movieMap.put("discountID", movie.getDiscountID());

            if (movie.getDirectorID() != null) {
                movieMap.put("directorName", movieDAO.getDirectorName(movie.getDirectorID()));
            } else {
                movieMap.put("directorName", "Unknown Director");
            }
            if (movie.getStudioID() != null) {
                movieMap.put("studioName", movieDAO.getStudioName(movie.getStudioID()));
            } else {
                movieMap.put("studioName", "Unknown Studio");
            }

            List<Act> acts = movie.getActs();
            logger.info("Movie ID: {}, Acts size: {}", movie.getMovieID(), acts.size());
            List<Map<String, Object>> actors = acts.stream()
                    .filter(act -> act.getActor() != null && act.getActor().getName() != null)
                    .map(act -> {
                        Map<String, Object> actorMap = new HashMap<>();
                        actorMap.put("actorID", act.getActor().getActorID());
                        actorMap.put("name", act.getActor().getName());
                        actorMap.put("role", act.getRole() != null ? act.getRole() : "Supporting Actor");
                        return actorMap;
                    })
                    .collect(Collectors.toList());
            movieMap.put("actors", actors);
            logger.info("Movie ID: {}, Actors mapped: {}", movie.getMovieID(), actors);

            return movieMap;
        }).toList();

        return ResponseEntity.ok(movieMaps);
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO(movie.getTitle(), movie.getPosterImageURL());
        dto.setMovieID(movie.getMovieID());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setGenre(movie.getGenre());
        dto.setPrice(movie.getPrice());
        return dto;
    }

    @PutMapping("/manager/movies/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable("id") Long movieID,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "posterImageURL", required = false) String posterImageURL,
            @RequestParam(value = "releaseDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDate,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "discountID", required = false) Long discountID,
            @RequestParam(value = "directorID", required = false) Long directorID,
            @RequestParam(value = "studioID", required = false) Long studioID) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (title != null && title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Movie title cannot be empty");
                return ResponseEntity.badRequest().body(response);
            }
            Movie existingMovie = movieDAO.findById(movieID);
            if (existingMovie == null) {
                response.put("success", false);
                response.put("message", "Movie not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            movieDAO.updateMovie(movieID, title, posterImageURL, releaseDate, price, genre, discountID, directorID, studioID);
            response.put("success", true);
            response.put("message", "Movie updated successfully");
            response.put("movieId", movieID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating movie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/manager/actors")
    public String showActorPage(Model model) {
        List<Actor> actors = actorDAO.findAllActors();
        model.addAttribute("actors", actors);
        return "manager-actors";
    }

    @GetMapping("/actors")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getActors(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender) {
        List<Actor> actors;
        if (name != null && !name.trim().isEmpty()) {
            actors = actorDAO.findActorsByPartialName(name.trim());
        } else if (gender != null && !gender.trim().isEmpty()) {
            actors = actorDAO.findAllActors().stream()
                    .filter(actor -> gender.equalsIgnoreCase(actor.getGender()))
                    .toList();
        } else {
            actors = actorDAO.findAllActors();
        }

        List<Map<String, Object>> actorMaps = actors.stream().map(actor -> {
            Map<String, Object> actorMap = new HashMap<>();
            actorMap.put("actorID", actor.getActorID());
            actorMap.put("name", actor.getName());
            actorMap.put("imageURL", actor.getImageURL() != null ? actor.getImageURL() : "");
            actorMap.put("gender", actor.getGender());
            actorMap.put("dob", actor.getDob());
            actorMap.put("bio", actor.getBio());
            actorMap.put("rank", actor.getRank());
            return actorMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(actorMaps);
    }

    @PostMapping("/manager/actors")
    @ResponseBody
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> addActor(
            @RequestParam("name") String name,
            @RequestParam(value = "imageURL", required = false) String imageURL,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "rank", required = false) Integer rank) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Actor name is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (imageURL == null || imageURL.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Image URL is required");
                return ResponseEntity.badRequest().body(response);
            }
            List<Actor> existingActors = actorDAO.findAllActors();
            if (existingActors.stream().anyMatch(a -> imageURL.equals(a.getImageURL()))) {
                response.put("success", false);
                response.put("message", "Image URL must be unique");
                return ResponseEntity.badRequest().body(response);
            }
            Actor actor = new Actor();
            actor.setName(name.trim());
            actor.setImageURL(imageURL.trim());
            if (gender != null && !gender.trim().isEmpty()) {
                actor.setGender(gender.trim());
            }
            actor.setDob(dob);
            if (bio != null && !bio.trim().isEmpty()) {
                actor.setBio(bio.trim());
            }
            if (rank != null && rank > 0) {
                actor.setRank(rank);
            }
            actorDAO.addActor(actor);
            response.put("success", true);
            response.put("message", "Actor added successfully");
            response.put("actorId", actor.getActorID());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding actor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/manager/actors/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> updateActor(
            @PathVariable("id") Long actorID,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "imageURL", required = false) String imageURL,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam(value = "bio", required = false) String bio) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (name != null && name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Actor name cannot be empty");
                return ResponseEntity.badRequest().body(response);
            }
            Actor existingActor = actorDAO.findById(actorID);
            if (existingActor == null) {
                response.put("success", false);
                response.put("message", "Actor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (imageURL != null && !imageURL.trim().isEmpty()) {
                List<Actor> otherActors = actorDAO.findAllActors().stream()
                        .filter(a -> !a.getActorID().equals(actorID))
                        .toList();
                if (otherActors.stream().anyMatch(a -> imageURL.equals(a.getImageURL()))) {
                    response.put("success", false);
                    response.put("message", "Image URL must be unique");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            if (name != null && !name.trim().isEmpty()) {
                existingActor.setName(name.trim());
            }
            if (imageURL != null && !imageURL.trim().isEmpty()) {
                existingActor.setImageURL(imageURL.trim());
            }
            if (gender != null) {
                existingActor.setGender(gender.trim().isEmpty() ? null : gender.trim());
            }
            existingActor.setDob(dob);
            if (bio != null) {
                existingActor.setBio(bio.trim().isEmpty() ? null : bio.trim());
            }
            actorDAO.updateActor(existingActor);
            response.put("success", true);
            response.put("message", "Actor updated successfully");
            response.put("actorId", actorID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating actor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/manager/studios")
    public String showStudioPage(Model model) {
        List<Studio> studio = studioDAO.findAllStudios();
        model.addAttribute("studio", studio);
        return "manager-studios";
    }

    @GetMapping("/manager/studios/add")
    @PreAuthorize("hasRole('MANAGER')")
    public String showAddStudioForm(Model model) {
        model.addAttribute("studio", new Studio());
        return "studio-form";
    }

    @GetMapping("/manager/studios/edit/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String showEditStudioForm(@PathVariable Long id, Model model) {
        Studio studio = studioDAO.findById(id);
        model.addAttribute("studio", studio != null ? studio : new Studio());
        return "studio-form";
    }

    @PostMapping("/manager/studios/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveStudio(@ModelAttribute Studio studio) {
        if (studio.getStudioID() == null) {
            studioDAO.addStudio(studio);
        } else {
            studioDAO.updateStudio(studio);
        }
        return "redirect:/manager/studios";
    }

    @GetMapping("/studios")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Studio>> getStudios(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "country", required = false) String country) {
        List<Studio> studios;
        if (name != null && !name.trim().isEmpty()) {
            studios = studioDAO.findStudiosByPartialName(name);
        } else if (country != null && !country.trim().isEmpty()) {
            studios = studioDAO.findStudiosByCountry(country);
        } else {
            studios = studioDAO.findAllStudios();
        }
        return ResponseEntity.ok(studios);
    }

    @PostMapping("/manager/studios")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> addStudio(
            @RequestPart("name") String name,
            @RequestPart(value = "country", required = false) String country,
            @RequestPart(value = "year", required = false) String year) {
        try {
            Studio studio = new Studio();
            studio.setName(name);
            studio.setCountry(country != null && !country.isEmpty() ? country : null);
            studio.setYear(year != null && !year.isEmpty() ? Integer.parseInt(year) : 0);
            studioDAO.addStudio(studio);
            return ResponseEntity.ok(Map.of("success", true, "message", "Studio added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Failed to add studio: " + e.getMessage()));
        }
    }

    @PutMapping("/manager/studios/{studioID}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> updateStudio(
            @PathVariable Long studioID,
            @RequestPart("name") String name,
            @RequestPart(value = "country", required = false) String country) {
        try {
            Studio studio = studioDAO.findById(studioID);
            if (studio == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Studio not found"));
            }
            studio.setName(name);
            studio.setCountry(country != null && !country.isEmpty() ? country : null);
            studioDAO.updateStudio(studio);
            return ResponseEntity.ok(Map.of("success", true, "message", "Studio updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Failed to update studio: " + e.getMessage()));
        }
    }

    @GetMapping("/manager/directors")
    public String showDirectorPage(Model model) {
        List<Director> director = directorDAO.findAllDirectors();
        model.addAttribute("director", director);
        return "manager-directors";
    }

    @GetMapping("/directors")
    public ResponseEntity<List<Director>> getDirectors(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "genre", required = false) String genre) {
        List<Director> directors;
        if (name != null && !name.trim().isEmpty()) {
            directors = directorDAO.findDirectorsByPartialName(name.trim());
        } else if (genre != null && !genre.trim().isEmpty()) {
            directors = directorDAO.findDirectorsByGenre(genre.trim());
        } else {
            directors = directorDAO.findAllDirectors();
        }
        return ResponseEntity.ok(directors);
    }

    @PostMapping("/manager/directors")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addDirector(
            @RequestParam("name") String name,
            @RequestParam("imageURL") String imageURL,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "rank", required = false) Integer rank,
            @RequestParam(value = "bio", required = false) String bio) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (name == null || name.trim().isEmpty() || imageURL == null || imageURL.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Name and Image URL are required.");
                return ResponseEntity.badRequest().body(response);
            }

            if (directorDAO.findAllDirectors().stream().anyMatch(d -> d.getImageURL().equals(imageURL))) {
                response.put("success", false);
                response.put("message", "Image URL must be unique.");
                return ResponseEntity.badRequest().body(response);
            }

            Director director = new Director();
            director.setName(name.trim());
            director.setImageURL(imageURL.trim());
            director.setGender(gender != null ? gender.trim() : null);
            director.setDob(dob != null && !dob.isEmpty() ? LocalDate.parse(dob) : null);
            director.setRank(rank != null ? rank : 0);
            director.setBio(bio != null ? bio.trim() : null);

            directorDAO.addDirector(director);

            response.put("success", true);
            response.put("message", "Director added successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding director: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/manager/directors/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateDirector(
            @PathVariable("id") Long directorID,
            @RequestParam("name") String name,
            @RequestParam("imageURL") String imageURL,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "rank", required = false) Integer rank,
            @RequestParam(value = "bio", required = false) String bio) {
        Map<String, Object> response = new HashMap<>();
        try {
            Director existingDirector = directorDAO.findById(directorID);
            if (existingDirector == null) {
                response.put("success", false);
                response.put("message", "Director not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (name == null || name.trim().isEmpty() || imageURL == null || imageURL.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Name and Image URL are required.");
                return ResponseEntity.badRequest().body(response);
            }

            if (!imageURL.equals(existingDirector.getImageURL()) &&
                directorDAO.findAllDirectors().stream()
                    .anyMatch(d -> !d.getDirectorID().equals(directorID) && d.getImageURL().equals(imageURL))) {
                response.put("success", false);
                response.put("message", "Image URL must be unique.");
                return ResponseEntity.badRequest().body(response);
            }

            existingDirector.setName(name.trim());
            existingDirector.setImageURL(imageURL.trim());
            existingDirector.setGender(gender != null ? gender.trim() : null);
            existingDirector.setDob(dob != null && !dob.isEmpty() ? LocalDate.parse(dob) : null);
            existingDirector.setRank(rank != null ? rank : 0);
            existingDirector.setBio(bio != null ? bio.trim() : null);

            directorDAO.updateDirector(existingDirector);

            response.put("success", true);
            response.put("message", "Director updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating director: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    @PostMapping("/manager/movies/{movieID}/actor")
    @ResponseBody
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> addActorsToMovie(
            @PathVariable("movieID") Long movieID,
            @RequestParam("actorIDs") List<Long> actorIDs,
            @RequestParam("roles") List<String> roles) {
        Map<String, Object> response = new HashMap<>();
        try {
            Movie movie = movieDAO.findById(movieID);
            if (movie == null) {
                response.put("success", false);
                response.put("message", "Movie not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (actorIDs.size() != roles.size()) {
                response.put("success", false);
                response.put("message", "Number of actor IDs and roles must match");
                return ResponseEntity.badRequest().body(response);
            }

            List<String> errors = new ArrayList<>();
            List<Act> actsToSave = new ArrayList<>();
            for (int i = 0; i < actorIDs.size(); i++) {
                Long actorID = actorIDs.get(i);
                String role = roles.get(i).trim();

                Actor actor = actorDAO.findById(actorID);
                if (actor == null) {
                    errors.add("Actor with ID " + actorID + " not found");
                    logger.warn("Attempted to add non-existent actor ID: {}", actorID);
                    continue;
                }

                if (actRepository.existsByActorIDAndMovieID(actorID, movieID)) {
                    errors.add("Actor with ID " + actorID + " is already associated with this movie");
                    logger.warn("Actor ID {} is already linked to movie ID {}", actorID, movieID);
                    continue;
                }

                Act act = new Act();
                act.setMovieID(movieID);
                act.setActorID(actorID);
                act.setRole(role.isEmpty() ? "Supporting Actor" : role);
                actsToSave.add(act);
            }

            if (!errors.isEmpty()) {
                response.put("success", false);
                response.put("message", "Some actors could not be added: " + String.join("; ", errors));
                logger.error("Failed to add some actors to movie ID {}: {}", movieID, String.join("; ", errors));
                return ResponseEntity.badRequest().body(response);
            }

            for (Act act : actsToSave) {
                actRepository.save(act);
            }

            response.put("success", true);
            response.put("message", "Actors added to movie successfully");
            logger.info("All actors successfully added to movie ID: {}", movieID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding actors to movie: " + e.getMessage());
            logger.error("Exception occurred while adding actors to movie ID {}: {}", movieID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/actor")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllActors(
            @RequestParam(value = "name", required = false) String name) {
        List<Actor> actors;
        if (name != null && !name.trim().isEmpty()) {
            actors = actorDAO.findActorsByPartialName(name.trim());
        } else {
            actors = actorDAO.findAllActors();
        }

        List<Map<String, Object>> actorMaps = actors.stream().map(actor -> {
            Map<String, Object> actorMap = new HashMap<>();
            actorMap.put("actorID", actor.getActorID());
            actorMap.put("name", actor.getName());
            actorMap.put("imageURL", actor.getImageURL() != null ? actor.getImageURL() : "");
            return actorMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(actorMaps);
    }
}
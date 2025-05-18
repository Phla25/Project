package Project.ChauPhim.Controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Project.ChauPhim.DAOs.ActorDAO;
import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.DAOs.DirectorDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.DAOs.StudioDAO;
import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Customer;
import Project.ChauPhim.Entities.Director;
import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Entities.Studio;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MovieDAO movieDAO; 

    @Autowired
    private DirectorDAO directorDAO;
    
    @Autowired
    private StudioDAO studioDAO;

    @Autowired
    private ActorDAO actorDAO;

    @GetMapping("/sign-in-customer")
    public String showSignInPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "sign-in-customer";
    }
    
    @PostMapping("/sign-in-customer")
    @Transactional
    public String processRegisterManager(
        @ModelAttribute("customer") Customer customer,
        Model model
    ) {
        try {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customerDAO.addCustomer(customer);
            return "redirect:/login-customer?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "sign-in-customer";
        }
    }

    @GetMapping("/login-customer")
    public String showLoginPage() {
        return "login-customer"; 
    }

    // hien tai login xong se redirect vao trang profile
    @PostMapping("/login-customer")
    public String processLogin(@RequestParam String username, 
                           @RequestParam String password, 
                           Model model) {
        try {
            Customer customer = customerDAO.findByUserName(username);
            if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
                System.out.println("Đăng nhập thành công!");
                return "redirect:/customer/profile"; // Chuyển sang controller hiển thị profile
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu sai!");
                return "login-customer";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "login-customer";
        }
    }

    // xem thong tin ca nhan
    @GetMapping("/customer/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            System.out.println("Username từ Principal: " + username);

            Customer customer = customerDAO.findByUserName(username);
            model.addAttribute("customer", customer);
            return "customer-profile";
        } else {
            return "redirect:/login-customer";
        }
    }
    
    @PostMapping("/customer/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updateCustomerInfo(
        @ModelAttribute("customer") Customer formCustomer,
        Authentication authentication,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                // Cập nhật thông tin từ form
                customerDAO.updateCustomer(
                    username, 
                    formCustomer.getEmail(), 
                    formCustomer.getName(),
                    formCustomer.getGender(),
                    formCustomer.getNationality(),
                    formCustomer.getDob()
                );
                
                redirectAttributes.addFlashAttribute("successMessage", "Thông tin đã được cập nhật thành công!");
                return "redirect:/customer/profile"; 
            }
            catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                // Lấy lại thông tin customer từ DB để hiển thị
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);
                return "customer-profile";
            }
        }
        else {
            return "redirect:/login-customer";
        }
    }

    @GetMapping("/customer/membership/register")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showMembershipRegistration(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("Username: " + username);
            try {
                // Lấy số dư hiện tại
                BigDecimal currentBalance = customerDAO.getCustomerBalance(username);
                model.addAttribute("currentBalance", currentBalance);

                return "membership-registration";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin tài khoản: " + e.getMessage());
                return "membership-registration";
            }
        } else {
            return "redirect:/login-customer";
        }
    }

    @PostMapping("/customer/membership/upgrade")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String upgradeMembership(
        @RequestParam("membershipType") int membershipType,
        Authentication authentication, 
        RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                // Kiểm tra membershipType hợp lệ (1: vàng, 2: kim cương)
                if (membershipType != 1 && membershipType != 2) {
                    redirectAttributes.addFlashAttribute("error", "Loại hội viên không hợp lệ");
                    return "redirect:/customer/membership/register";
                }

                // Định nghĩa phí hội viên theo cấp bậc
                BigDecimal goldFee = new BigDecimal("200"); 
                BigDecimal diamondFee = new BigDecimal("500"); 

                BigDecimal membershipFee = membershipType == 1 ? goldFee : diamondFee;

                // Lấy số dư hiện tại
                BigDecimal currentBalance = customerDAO.getCustomerBalance(username);

                // Kiểm tra số dư có đủ không
                if (currentBalance.compareTo(membershipFee) < 0) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Số dư không đủ để nâng cấp hội viên. Cần tối thiểu " + 
                        membershipFee + " $ để nâng cấp.");
                    return "redirect:/customer/membership/register";
                }

                // Trừ tiền từ số dư
                BigDecimal newBalance = currentBalance.subtract(membershipFee);
                customerDAO.updateBalance(username, newBalance);

                customerDAO.updateRank(username, membershipType);

                String membershipName = membershipType == 1 ? "Vàng" : "Kim Cương";
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Chúc mừng! Bạn đã trở thành hội viên " + membershipName + 
                    ". Số dư hiện tại: " + newBalance + " $");

                return "redirect:/customer/profile";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Không thể nâng cấp tài khoản: " + e.getMessage());
                return "redirect:/customer/membership/register";
            }
        } else {
            return "redirect:/login-customer";
        }
    }

    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showDashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                // Lấy thông tin khách hàng
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);

                // Lấy số dư
                BigDecimal balance = customerDAO.getCustomerBalance(username);
                model.addAttribute("balance", balance);

                // Lấy cấp bậc hội viên
                int rank = customer.getRank();
                String rankName;
                switch (rank) {
                    case 0:
                        rankName = "Thành viên thường";
                        break;
                    case 1:
                        rankName = "Hội viên Vàng";
                        break;
                    case 2:
                        rankName = "Hội viên Kim Cương";
                        break;
                    default:
                        rankName = "Không xác định";
                }
                model.addAttribute("rankName", rankName);

                // Lấy danh sách phim mới nhất - tích hợp từ CustomerDashboardDAO (đã bỏ đi từ trước)
                List<Movie> latestMovies = movieDAO.findLatestMovie(7);
                for (Movie movie : latestMovies) {
                    System.out.println("Movie: " + movie.getTitle() + ", ReleaseDate: " + movie.getReleaseDate());
                }
                model.addAttribute("latestMovies", latestMovies);

                return "customer-dashboard";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin tài khoản: " + e.getMessage());
                return "customer-profile";
            }
        } else {
            return "redirect:/login-customer";
        }
    }

    @GetMapping("/customer/movies")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showAllMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication,
            Model model) {
            
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                // Get customer info
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);

                // Get all movies (we'll implement filtering and pagination later)
                List<Movie> allMovies = movieDAO.findAllMovies();

                // Apply search filter if provided
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    allMovies = allMovies.stream()
                            .filter(movie -> movie.getTitle().toLowerCase().contains(searchLower))
                            .collect(Collectors.toList());
                }

                // Apply genre filter if provided
                if (genre != null && !genre.trim().isEmpty()) {
                    allMovies = allMovies.stream()
                            .filter(movie -> genre.equals(movie.getGenre()))
                            .collect(Collectors.toList());
                }

                // Apply sorting if provided
                if (sort != null && !sort.isEmpty()) {
                    String[] sortParams = sort.split(",");
                    String sortField = sortParams[0];
                    boolean ascending = sortParams.length > 1 && "asc".equals(sortParams[1]);

                    Comparator<Movie> comparator = null;
                    switch (sortField) {
                        case "price":
                            comparator = Comparator.comparing(Movie::getPrice);
                            break;
                        case "releaseDate":
                            comparator = Comparator.comparing(Movie::getReleaseDate);
                            break;
                        case "title":
                            comparator = Comparator.comparing(Movie::getTitle);
                            break;
                        default:
                            comparator = Comparator.comparing(Movie::getReleaseDate);
                    }

                    if (!ascending) {
                        comparator = comparator.reversed();
                    }

                    allMovies = allMovies.stream()
                            .sorted(comparator)
                            .collect(Collectors.toList());
                }

                // Calculate pagination
                int pageSize = 12; // 12 movies per page
                int totalMovies = allMovies.size();
                int totalPages = (int) Math.ceil((double) totalMovies / pageSize);

                // Adjust page if out of bounds
                if (page < 1) page = 1;
                if (page > totalPages && totalPages > 0) page = totalPages;

                // Get movies for current page
                int fromIndex = (page - 1) * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, totalMovies);

                List<Movie> pagedMovies;
                if (fromIndex < totalMovies) {
                    pagedMovies = allMovies.subList(fromIndex, toIndex);
                } else {
                    pagedMovies = new ArrayList<>();
                }

                // Load directors and studios for each movie
                for (Movie movie : pagedMovies) {
                    if (movie.getDirectorID() != null) {
                        Director director = directorDAO.findById(movie.getDirectorID());
                        movie.setDirector(director);
                    }

                    if (movie.getStudioID() != null) {
                        Studio studio = studioDAO.findById(movie.getStudioID());
                        movie.setStudio(studio);
                    }
                }

                // Add attributes to model
                model.addAttribute("movies", pagedMovies);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);

                return "customer-movies";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy danh sách phim: " + e.getMessage());
                return "customer/dashboard";
            }
        } else {
            return "redirect:/login-customer";
        }
    }

    @GetMapping("/customer/movies/{movieID}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showMovieDetails(@PathVariable Long movieID, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
                System.out.println("Accessing movie details for ID: " + movieID);
            try {
                Movie movie = movieDAO.findById(movieID);
                if (movie == null) {
                    System.out.println("Cant find movie with ID: " + movieID);
                    model.addAttribute("error", "Không tìm thấy phim với ID: " + movieID);
                    return "redirect:/customer/movies";
                }

                // Load director if exists
                if (movie.getDirectorID() != null) {
                    Director director = directorDAO.findById(movie.getDirectorID());
                    movie.setDirector(director);
                }

                // Load studio if exists
                if (movie.getStudioID() != null) {
                    Studio studio = studioDAO.findById(movie.getStudioID());
                    movie.setStudio(studio);
                }

                // Load actors for this movie
                List<Actor> actors = actorDAO.findActorsByMovieId(movieID);
                movie.setActors(actors);

                model.addAttribute("movie", movie);

                // Add customer info
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);

                return "customer-movie-details";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin phim: " + e.getMessage());
                return "redirect:/customer/movies";
            }
        } else {
            return "redirect:/login-customer";
        }
    }

    @GetMapping("/customer/movies/{movieID}/buy")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String buyMovie(@PathVariable Long movieID, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Movie movie = movieDAO.findById(movieID);
                if (movie == null) {
                    model.addAttribute("error", "Không tìm thấy phim với ID: " + movieID);
                    return "redirect:/customer/movies";
                }

                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);

                // Add logic for buying a movie
                // Check if customer has enough balance
                // Process the purchase
                // Update customer balance

                model.addAttribute("movie", movie);
                model.addAttribute("customer", customer);

                return "customer/buy-movie"; // chua co buy-movie, co the add to cart
            } catch (Exception e) {
                model.addAttribute("error", "Không thể xử lý giao dịch: " + e.getMessage());
                return "redirect:/customer/movies/" + movieID;
            }
        } else {
            return "redirect:/login-customer";
        }
    }

}
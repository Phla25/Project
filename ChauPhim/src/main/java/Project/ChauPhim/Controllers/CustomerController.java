package Project.ChauPhim.Controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.Entities.Customer;
import Project.ChauPhim.Entities.Movie;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MovieDAO movieDAO; 

    @GetMapping("/customer-register")
    public String showSignInPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-register";
    }
    
    @PostMapping("/customer-register")
    @Transactional
    public String processRegisterManager(@ModelAttribute("customer") Customer customer, Model model) {
        try {
            // 1. Kiểm tra xem username đã tồn tại trong database chưa
            Customer existingCustomer = customerDAO.findByUserName(customer.getUsername());

            if (existingCustomer != null) {
                // Nếu username đã tồn tại, thêm thông báo lỗi vào model và trả về trang đăng ký
                model.addAttribute("error", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                return "customer-register";
            }

            // 2. Nếu username chưa tồn tại, tiến hành mã hóa mật khẩu và thêm người dùng
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customerDAO.addCustomer(customer);
            return "redirect:/customer-login?registered=true";

        } catch (Exception e) {
            // Xử lý các lỗi khác (ví dụ: lỗi database)
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
            // Quan trọng: Đánh dấu transaction là rollback-only để tránh dữ liệu không nhất quán
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "customer-register";
        }
    }

    @GetMapping("/customer-login")
    public String showLoginPage() {
        return "customer-login"; 
    }

    // hien tai login xong se redirect vao trang profile
    @PostMapping("/customer-login")
    public String processLogin(@RequestParam String username, 
                           @RequestParam String password, 
                           Model model) {
        try {
            Customer customer = customerDAO.findByUserName(username);
            if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
                System.out.println("Đăng nhập thành công!");
                return "redirect:/customer/dashboard"; // Chuyển sang controller hiển thị dashboard
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu sai!");
                return "customer-login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "customer-login";
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
            return "redirect:/customer-login";
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
            return "redirect:/customer-login";
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
                model.addAttribute("customer", customerDAO.findByUserName(username));

                return "membership-registration";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin tài khoản: " + e.getMessage());
                return "membership-registration";
            }
        } else {
            return "redirect:/customer-login";
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
            return "redirect:/customer-login";
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
            return "redirect:/customer-login";
        }
    }

    @GetMapping("/customer/movies/{movieId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showMovieDetails(@PathVariable Long movieId, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Movie movie = movieDAO.findById(movieId);
                if (movie == null) {
                    model.addAttribute("error", "Không tìm thấy phim với ID: " + movieId);
                    return "redirect:/customer/movies";
                }

                model.addAttribute("movie", movie);

                // Add customer info if needed
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);

                return "customer/movie-details";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin phim: " + e.getMessage());
                return "redirect:/customer/movies";
            }
        } else {
            return "redirect:/customer-login";
        }
    }

    @GetMapping("/customer/movies/{movieId}/buy")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String buyMovie(@PathVariable Long movieId, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Movie movie = movieDAO.findById(movieId);
                if (movie == null) {
                    model.addAttribute("error", "Không tìm thấy phim với ID: " + movieId);
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
                return "redirect:/customer/movies/" + movieId;
            }
        } else {
            return "redirect:/customer-login";
        }
    }

}
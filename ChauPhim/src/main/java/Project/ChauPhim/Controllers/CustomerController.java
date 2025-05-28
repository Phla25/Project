package Project.ChauPhim.Controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import Project.ChauPhim.DAOs.ActorDAO;
import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.DAOs.MovieDAO;
import Project.ChauPhim.DAOs.OrderDAO;
import Project.ChauPhim.DAOs.StudioDAO;
import Project.ChauPhim.Entities.Actor;
import Project.ChauPhim.Entities.Customer;
import Project.ChauPhim.Entities.Movie;
import Project.ChauPhim.Entities.Orders;
import Project.ChauPhim.Entities.Studio;
import Project.ChauPhim.Models.CustomerDTO;
import Project.ChauPhim.Models.MovieDTO;
import Project.ChauPhim.Models.OrderDTO;
import Project.ChauPhim.Models.StudioDTO;
import jakarta.servlet.http.HttpSession;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MovieDAO movieDAO; 

    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private ActorDAO actorDAO;

    @Autowired
    private StudioDAO studioDAO;

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
                return "redirect:/customer-dashboard"; // Chuyển sang controller hiển thị dashboard
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


    @GetMapping("/customer/gallery")
    @PreAuthorize("hasRole('CUSTOMER')") // Nếu bạn muốn chỉ khách hàng mới xem được
    public String showGallery(Authentication authentication, Model model) {
        String username = authentication.getName();
        Customer customer = customerDAO.findByUserName(username);
        model.addAttribute("customer", customer);
        return "customer-gallery"; // Trả về tên file template
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

                System.out.println("Dang o trong Dashboard");
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
                List<Movie> movies = movieDAO.findAllMovies();
                model.addAttribute("movies", movies);

                return "customer-dashboard";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thông tin tài khoản: " + e.getMessage());
                return "customer-profile";
            }
        } else {
            return "redirect:/customer-login";
        }
    }
    
    @GetMapping("/customer/orders/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String addToCart(@RequestParam("movieId") Long movieId,
                           Authentication authentication,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

    System.out.println("=== CONTROLLER ĐƯỢC GỌI ===");
    System.out.println("MovieID: " + movieId);
    System.out.println("Session ID: " + session.getId());
    System.out.println("User: " + (authentication != null ? authentication.getName() : "null"));
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                Movie movie = movieDAO.findById(movieId);

                System.out.println("Da Vao trong /create ");
                if (movie == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim.");
                    return "redirect:/customer-dashboard";
                }

                System.out.println("Qua /create, chuan bi vao /orders neu existing != null");
                // Kiểm tra xem đã đặt mua phim này chưa (trong orders thực tế)
                Orders existingOrder = orderDAO.findByCustomerIdAndMovieId(customer.getCustomerID(), movieId);
                if (existingOrder != null) {
                    System.out.println("Da co existing Order va redirect");
                    redirectAttributes.addFlashAttribute("info", "Bạn đã đặt mua phim này rồi.");
                    return "redirect:/customer/orders/checkout";
                }
                
                System.out.println("chua co Cart");
                System.out.println("=== BEFORE ADDING TO CART ===");
                // Lấy cart từ session
                @SuppressWarnings("unchecked")
                List<Long> cart = (List<Long>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                }

                // Kiểm tra xem phim đã có trong cart chưa
                if (!cart.contains(movieId)) {
                    cart.add(movieId);
                    session.setAttribute("cart", cart);
                    System.out.println("Added movieId " + movieId + " to cart");
                    System.out.println("Cart after adding: " + cart);
                    redirectAttributes.addFlashAttribute("success", "Đã thêm phim vào giỏ hàng!");
                } else {
                    redirectAttributes.addFlashAttribute("info", "Phim đã có trong giỏ hàng!");
                }

                // Redirect về trang cart để hiển thị
                return "redirect:/customer/cart";

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
                return "redirect:/customer-dashboard";
            }
        } else {
            return "redirect:/customer-login";
        }
    }

// Hiển thị trang cart
    @GetMapping("/customer/cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showCart(Authentication authentication, 
                          HttpSession session,
                          Model model) {
        System.out.println("=== SHOW CART SESSION DEBUG ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session isNew: " + session.isNew());
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                
                // Lấy cart từ session
                @SuppressWarnings("unchecked")
                List<Long> cart = (List<Long>) session.getAttribute("cart");
                
                System.out.println("Truy cap vao trang Cart");
                List<MovieDTO> cartMovies = new ArrayList<>();
                double totalPrice = 0;
                
                if (cart != null && !cart.isEmpty()) {
                    for (Long movieId : cart) {
                        Movie movie = movieDAO.findById(movieId);
                        if (movie != null) {
                            MovieDTO movieDTO = convertToMovieDTO(movie);
                            cartMovies.add(movieDTO);
                            totalPrice += movie.getPrice();
                        }
                    }
                }
                
                model.addAttribute("cartMovies", cartMovies);
                model.addAttribute("totalPrice", totalPrice);

                model.addAttribute("customer", customer);

                
                return "customer-cart";
                
            } catch (Exception e) {
                model.addAttribute("error", "Không thể hiển thị giỏ hàng: " + e.getMessage());
                return "customer-dashboard";
            }
        } else {
            return "redirect:/customer-login";
        }
    }

// Xóa phim khỏi cart
    @PostMapping("/customer/cart/remove")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String removeFromCart(@RequestParam("movieId") Long movieId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
                                
        System.out.println("=== DEBUG REMOVE FROM CART ===");
        System.out.println("MovieID to remove: " + movieId);
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session isNew: " + session.isNew());
                                
        @SuppressWarnings("unchecked")
        List<Long> cart = (List<Long>) session.getAttribute("cart");
                                
        System.out.println("Cart before remove: " + cart);
                                
        if (cart != null) {
            boolean removed = cart.removeIf(id -> id.equals(movieId));

            System.out.println("Removed: " + removed);
            System.out.println("Cart after remove: " + cart);

            if (removed) {
                session.setAttribute("cart", cart);
                redirectAttributes.addFlashAttribute("success", "Đã xóa phim khỏi giỏ hàng!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim trong giỏ hàng!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
        }

        return "redirect:/customer/cart";
    }

    @GetMapping("/customer/orders/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showCheckoutPage(Authentication authentication,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);

                @SuppressWarnings("unchecked")
                List<Long> cart = (List<Long>) session.getAttribute("cart");

                if (cart == null || cart.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
                    return "redirect:/customer/cart";
                }

                // Lấy thông tin các phim trong cart
                List<Movie> cartMovies = new ArrayList<>();
                double totalPrice = 0;

                for (Long movieId : cart) {
                    Movie movie = movieDAO.findById(movieId);
                    if (movie != null) {
                        cartMovies.add(movie);
                        totalPrice += movie.getPrice();
                    }
                }

                model.addAttribute("cartMovies", cartMovies);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("customer", customer);

                return "customer-checkout"; // Hiển thị trang checkout

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
                return "redirect:/customer/cart";
            }
        } else {
            return "redirect:/customer-login";

        }
    }

    @PostMapping("/customer/orders/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String processCheckout(Authentication authentication,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);

                @SuppressWarnings("unchecked")
                List<Long> cart = (List<Long>) session.getAttribute("cart");

                if (cart == null || cart.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
                    return "redirect:/customer/cart";
                }

                // Tạo orders cho tất cả phim trong cart
                int successCount = 0;
                for (Long movieId : cart) {
                    Movie movie = movieDAO.findById(movieId);
                    if (movie != null) {
                        // Kiểm tra xem đã order chưa
                        Orders existingOrder = orderDAO.findByCustomerIdAndMovieId(customer.getCustomerID(), movieId);
                        if (existingOrder == null) {
                            Orders newOrder = new Orders();
                            newOrder.setMovieID(movieId);
                            newOrder.setCustomerID(customer.getCustomerID());
                            newOrder.setDate(LocalDate.now());
                            newOrder.setRate(0);

                            orderDAO.save(newOrder);
                            successCount++;
                        }
                    }
                }

                // Xóa cart sau khi thanh toán thành công
                session.removeAttribute("cart");
                System.out.println("Xoa cart");
                redirectAttributes.addFlashAttribute("success", 
                    "Đặt mua thành công " + successCount + " phim!");
                    System.out.println("Mua phim thanh cong!");
                return "redirect:/customer/cart";

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi thanh toán: " + e.getMessage());
                return "redirect:/customer/cart";
            }
        } else {
            return "redirect:/customer-login";
        }
    }
    
    // Hiển thị danh sách đơn hàng
    @GetMapping("/orders")
    public String showOrderList(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                
                System.out.println("Da vao cheo orders");
                // Lấy danh sách orders của customer
                List<Orders> orders = orderDAO.findByCustomerID(customer.getCustomerID());
                
                // Chuyển đổi sang OrderDTO list để hiển thị đầy đủ thông tin
                List<OrderDTO> orderDTOs = new ArrayList<>();
                for (Orders order : orders) {
                    OrderDTO dto = convertToOrderDTO(order);
                    orderDTOs.add(dto);
                }
                
                model.addAttribute("orders", orderDTOs);
                model.addAttribute("customer", customer);
                
                return "customer/order-list";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy danh sách đơn hàng: " + e.getMessage());
                return "customer-dashboard";
            }
        } else {
            return "redirect:/customer-login";
        }
    }
    
    /*@PostMapping("/orders/{orderId}/rate")
    public String rateMovie(@PathVariable Long orderId, 
                           @RequestParam int rating,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                
                Orders order = orderDAO.findById(orderId);
                if (order == null || !order.getCustomerID().equals(customer.getCustomerID())) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                    return "redirect:/customer/orders";
                }
                
                // Cập nhật rating (1-5 sao)
                if (rating >= 1 && rating <= 5) {
                    order.setRate(rating);
                    orderDAO.save(order);
                    redirectAttributes.addFlashAttribute("success", "Đã cập nhật đánh giá thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Đánh giá phải từ 1 đến 5 sao!");
                }
                
                return "redirect:/customer/orders";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Không thể cập nhật đánh giá: " + e.getMessage());
                return "redirect:/customer/orders";
            }
        } else {
            return "redirect:/customer-login";
        }
    }
    */ 
    /*@GetMapping("/orders/statistics")
    public String showOrderStatistics(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Customer customer = customerDAO.findByUserName(username);
                
                int totalMovieCount = orderDAO.countMoviesByCustomerID(customer.getCustomerID());
                double totalSpent = orderDAO.calculateTotalSpentByCustomerID(customer.getCustomerID());
                
                // Lấy orders gần đây (30 ngày)
                LocalDate thirtyDaysAgo = LocalDate.now().minus(30, ChronoUnit.DAYS);
                List<Orders> recentOrders = orderDAO.findByCustomerIDAndDateRange(
                    customer.getCustomerID(), thirtyDaysAgo, LocalDate.now());
                
                model.addAttribute("customer", customer);
                model.addAttribute("totalMovieCount", totalMovieCount);
                model.addAttribute("totalSpent", totalSpent);
                model.addAttribute("recentOrdersCount", recentOrders.size());
                model.addAttribute("averageSpentPerMovie", totalMovieCount > 0 ? totalSpent / totalMovieCount : 0);
                
                return "customer/order-statistics";
            } catch (Exception e) {
                model.addAttribute("error", "Không thể lấy thống kê: " + e.getMessage());
                return "redirect:/customer/orders";
            }
        } else {
            return "redirect:/customer-login";
        }
    }
    */ 

    // Helper methods để chuyển đổi giữa Entity và DTO
    private MovieDTO convertToMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO(movie.getTitle(), movie.getPosterImageURL());
        
        // Set movieID để có thể sử dụng trong các form
        dto.setMovieID(movie.getMovieID());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setGenre(movie.getGenre());
        dto.setPrice(movie.getPrice());
        
        return dto;
    }

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO(
            customer.getCustomerID(),
            customer.getUsername(), 
            null, 
            customer.getEmail(), 
            customer.getRank(), 
            customer.getDob()
        );
        
        return dto;
    }

    private Orders convertToOrderEntity(OrderDTO orderDTO) {
        Orders order = new Orders();
        order.setMovieID(orderDTO.getMovie().getMovieID()); // Sửa từ getMovieID()
        order.setCustomerID(orderDTO.getCustomerID()); // Sửa từ getMovieID()
        order.setDate(orderDTO.getDate());
        order.setRate(orderDTO.getRate());
        // Không set quantity và totalPrice vì Orders entity không có
        return order;
    }

    private OrderDTO convertToOrderDTO(Orders order) {
        // Lấy thông tin Movie và Customer từ database
        Movie movie = movieDAO.findById(order.getMovieID());
        Customer customer = customerDAO.findById(order.getCustomerID());

        // Tạo MovieDTO sử dụng method đã viết
        MovieDTO movieDTO = convertToMovieDTO(movie);

        // Tạo CustomerDTO sử dụng method đã viết
        CustomerDTO customerDTO = convertToCustomerDTO(customer);

        // Sử dụng constructor của OrderDTO
        OrderDTO dto = new OrderDTO(
            movieDTO,           // MovieDTO movie
            order.getDate(),    // LocalDate date
            customerDTO,        // CustomerDTO customer
            order.getRate(),    // int rate
            null,              // DiscountDTO discount (null vì không có)
            movie.getPrice(),   // double totalPrice (= giá phim vì quantity = 1)
            customer.getCustomerID()
        );

        return dto;
    }
}
package Project.ChauPhim.Controllers;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.Entities.Customer;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
            // ✅ KHÔNG cần session.setAttribute nữa
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

    // Sau này endpoint này sẽ xử lý việc nâng cấp rank
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
                    membershipFee + " VND để nâng cấp.");
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
}
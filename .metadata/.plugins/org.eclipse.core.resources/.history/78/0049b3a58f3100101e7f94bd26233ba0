package Project.ChauPhim.Controllers;

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

import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.Entities.Customer;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Hiển thị trang đăng ký
    @GetMapping("/sign-in-customer")
    public String showSignInPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "sign-in-customer";
    }
    
    // Xử lý đăng ký
    @PostMapping("/sign-in-customer")
    @Transactional
    public String processRegisterCustomer(
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

    // Hiển thị trang đăng nhập
    @GetMapping("/login-customer")
    public String showLoginPage() {
        return "login-customer"; 
    }

    // Xem thông tin cá nhân - chỉ cho ROLE_CUSTOMER
    @GetMapping("/customer/profile")
    @PreAuthorize("hasRole('CUSTOMER')") // Thêm phân quyền chi tiết nếu cần
    public String viewProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Customer customer = customerDAO.findByUserName(username);
            if (customer == null) {
                return "redirect:/login-customer";
            }
            model.addAttribute("customer", customer);
            return "customer-profile";
        } else {
            return "redirect:/login-customer";
        }
    }
    
    // Cập nhật thông tin cá nhân - chỉ cho ROLE_CUSTOMER
    @PostMapping("/customer/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updateCustomerInfo(
        @RequestParam String email,
        @RequestParam String name, 
        Authentication authentication,
        Model model
    ) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                customerDAO.updateEmailAndName(username, email, name);
                return "redirect:/customer/profile?success=true"; // redirect với success flag
            }
            catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                Customer customer = customerDAO.findByUserName(username);
                model.addAttribute("customer", customer);
                return "customer-profile";
            }
        }
        else {
            return "redirect:/login-customer";
        }
    }
}
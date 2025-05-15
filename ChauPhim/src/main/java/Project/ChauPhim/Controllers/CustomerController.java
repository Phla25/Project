package Project.ChauPhim.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.Entities.Customer;

@Controller
public class CustomerController {
    
    @Autowired
    private CustomerDAO customerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Đăng ký tài khoản
    @GetMapping("/sign-in-customer")
    public String showSignInPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "sign-in-customer";
    }
    
    @PostMapping("/sign-in-customer")
    @Transactional
    public String processRegister(
        @ModelAttribute("customer") Customer customer,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customerDAO.addCustomer(customer);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login-customer";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "sign-in-customer";
        }
    }

    // Đăng nhập
    @GetMapping("/login-customer")
    public String showLoginPage(
        @RequestParam(value = "registered", required = false) Boolean registered,
        Model model
    ) {
        if (registered != null && registered) {
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "login-customer";
    }

    @PostMapping("/login-customer")
    public String processLogin(
        @RequestParam String username, 
        @RequestParam String password,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Customer customer = customerDAO.findByUserName(username);
            
            if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
                // Lưu toàn bộ thông tin customer vào session
                session.setAttribute("currentCustomer", customer);
                
                // Redirect về trang profile
                return "redirect:/customer-profile";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác");
                return "redirect:/login-customer";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            return "redirect:/login-customer";
        }
    }

    // Trang profile
    @GetMapping("/customer-profile")
    public String showProfile(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("currentCustomer");
        
        if (customer == null) {
            return "redirect:/login-customer";
        }
        
        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    // Cập nhật thông tin
    @PostMapping("/customer-profile")
    public String updateProfile(
        @ModelAttribute("customer") Customer updatedCustomer,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Customer currentCustomer = (Customer) session.getAttribute("currentCustomer");
            
            if (currentCustomer == null) {
                return "redirect:/login-customer";
            }
            
            // Cập nhật thông tin
            customerDAO.updateCustomer(
                currentCustomer.getUsername(),
                updatedCustomer.getEmail(),
                updatedCustomer.getName(),
                updatedCustomer.getUsername()
            );
            
            // Cập nhật session
            currentCustomer.setEmail(updatedCustomer.getEmail());
            currentCustomer.setName(updatedCustomer.getName());
            session.setAttribute("currentCustomer", currentCustomer);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/customer-profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
            return "redirect:/customer-profile";
        }
    }
}
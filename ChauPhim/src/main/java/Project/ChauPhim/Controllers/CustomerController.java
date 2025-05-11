package Project.ChauPhim.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
}
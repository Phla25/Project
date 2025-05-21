package Project.ChauPhim.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import Project.ChauPhim.DAOs.ManagerDAO;
import Project.ChauPhim.Entities.Manager;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class ManagerController {
    @Autowired
    private ManagerDAO managerDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/sign-in-manager")
    public String showSignInPage(Model model) {
        model.addAttribute("manager", new Manager());
        return "sign-in-manager";
    }
    
    @PostMapping("/sign-in-manager")
    @Transactional
    public String processRegisterManager(
        @ModelAttribute("manager") Manager manager,
        Model model
    ) {
        try {
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            managerDAO.addManager(manager);
            return "redirect:/login-manager?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "sign-in-manager";
        }
    }
    
    @GetMapping("/login-manager")
    public String showLoginPage() {
        return "login-manager";
    }
}
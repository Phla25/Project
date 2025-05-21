package Project.ChauPhim.Services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Project.ChauPhim.DAOs.ManagerDAO;
import Project.ChauPhim.Entities.Manager;

@Service
public class CustomManagerDetailsService implements UserDetailsService {

    private final ManagerDAO managerDAO;

    public CustomManagerDetailsService(ManagerDAO managerDAO) {
        this.managerDAO = managerDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerDAO.findByUserName(username);
        if (manager == null) {
            throw new UsernameNotFoundException("Manager not found with username: " + username);
        }

        return User.builder()
                .username(manager.getUsername())
                .password(manager.getPassword()) // Password đã mã hóa
                .roles("MANAGER")
                .build();
    }
}
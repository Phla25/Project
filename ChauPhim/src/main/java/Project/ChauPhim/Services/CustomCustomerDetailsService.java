package Project.ChauPhim.Services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Project.ChauPhim.DAOs.CustomerDAO;
import Project.ChauPhim.Entities.Customer;

@Service
public class CustomCustomerDetailsService implements UserDetailsService {

    private final CustomerDAO customerDAO;

    public CustomCustomerDetailsService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerDAO.findByUserName(username);
        if (customer == null) {
            throw new UsernameNotFoundException("Customer not found with username: " + username);
        }

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword()) // Password đã mã hóa
                .roles("CUSTOMER")
                .build();
    }
}
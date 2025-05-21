package Project.ChauPhim.Services;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CustomCustomerDetailsService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerDAO.findByUserName(username);
        
        if (customer == null) {
            throw new UsernameNotFoundException("Không tìm thấy khách hàng: " + username);
        }
        
        // Tạo một UserDetails với ROLE_CUSTOMER
        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .roles("CUSTOMER") // Quan trọng: sử dụng ROLE_CUSTOMER
                .build();
    }
}
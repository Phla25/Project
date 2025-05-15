package Project.ChauPhim.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import Project.ChauPhim.Services.CustomCustomerDetailsService;
import Project.ChauPhim.Services.CustomManagerDetailsService;

@Configuration
public class WebSecurityConfig {

    private final CustomManagerDetailsService managerDetailsService;
    private final CustomCustomerDetailsService customerDetailsService;

    public WebSecurityConfig(CustomManagerDetailsService managerDetailsService,
                             CustomCustomerDetailsService customerDetailsService) {
        this.managerDetailsService = managerDetailsService;
        this.customerDetailsService = customerDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable()) // Chỉ tắt khi thực sự cần
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/sign-in-manager", 
                        "/sign-in-customer",
                        "/login-customer",
                        "/login-manager",
                        "/css/**", 
                        "/js/**"
                    ).permitAll()
                    .requestMatchers("/customer-profile").hasAnyRole("CUSTOMER", "MANAGER")
                    .anyRequest().authenticated()
                )
                .formLogin(login -> login
                    .loginPage("/login-customer") // Trang login mặc định
                    .loginProcessingUrl("/perform-login") // URL xử lý login
                    .defaultSuccessUrl("/customer-profile", true) // Bắt buộc chuyển hướng
                    .failureUrl("/login-customer?error=true")
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login-customer?logout=true")
                    .permitAll()
                )
                .userDetailsService(customerDetailsService); // Sử dụng customer làm mặc định

            return http.build();
        }
        // ... (giữ nguyên các bean khác)
    // AuthenticationProvider cho Manager
    @Bean
    public DaoAuthenticationProvider managerAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(managerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationProvider cho Customer
    @Bean
    public DaoAuthenticationProvider customerAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Cấu hình AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
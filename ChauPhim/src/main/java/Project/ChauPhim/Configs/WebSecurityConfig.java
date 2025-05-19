package Project.ChauPhim.Configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import Project.ChauPhim.Services.CustomCustomerDetailsService;
import Project.ChauPhim.Services.CustomManagerDetailsService;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
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
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
        .securityMatcher("/customer/**", "/customer-login", "/customer-register", "/process-customer-login", "/customer-profile")
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/customer-login", "/customer-register", "/process-customer-login", "/css/**", "/js/**").permitAll()
            .requestMatchers("/customer/**", "/customer-profile").hasRole("CUSTOMER")
            .anyRequest().authenticated()
        )
        .formLogin(login -> login
            .loginPage("/customer-login")
            .loginProcessingUrl("/process-customer-login")
            .defaultSuccessUrl("/customer/dashboard", true)  // Sửa thành đường dẫn /customer/profile thay vì /customer-profile
            .failureUrl("/customer-login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/customer/logout")
            .logoutSuccessUrl("/customer-login?logout=true")
            .permitAll()
        )
        .authenticationProvider(customerAuthenticationProvider());
        return http.build();
    }
    
    @Bean
    public SecurityFilterChain managerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/manager/**", "/login-manager", "/sign-in-manager")  // Áp dụng cho đường dẫn manager
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login-manager", "/sign-in-manager", "/css/**", "/js/**").permitAll()
                .requestMatchers("/manager/**").hasRole("MANAGER")  // Chỉ cho phép ROLE_MANAGER truy cập
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login-manager")
                .loginProcessingUrl("/process-login-manager")  // URL xử lý form đăng nhập
                .defaultSuccessUrl("/manager/dashboard", true) // Chuyển hướng sau khi đăng nhập thành công
                .failureUrl("/login-manager?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/manager/logout")
                .logoutSuccessUrl("/login-manager?logout=true")
                .permitAll()
            )
            .authenticationProvider(managerAuthenticationProvider());  // Chỉ định provider cho manager

        return http.build();
    }
    
    // Cấu hình cho các đường dẫn công khai
    @Bean
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/", "/css/**", "/js/**", "/images/**")  // Các đường dẫn công khai
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration,
            DaoAuthenticationProvider customerAuthenticationProvider,
            DaoAuthenticationProvider managerAuthenticationProvider
    ) throws Exception {
        return new ProviderManager(
                List.of(customerAuthenticationProvider, managerAuthenticationProvider)
        );
    }
}
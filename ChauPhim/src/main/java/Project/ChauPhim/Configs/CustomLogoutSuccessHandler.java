package Project.ChauPhim.Configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Kiểm tra logic tùy chỉnh
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))) {
            // Redirect tới login-manager nếu là MANAGER
            response.sendRedirect("/login-manager");
        } else {
            // Redirect tới login-customer nếu không phải MANAGER
            response.sendRedirect("/customer-login");
        }
    }
}
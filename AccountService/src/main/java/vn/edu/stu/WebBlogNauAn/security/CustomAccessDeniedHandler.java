package vn.edu.stu.WebBlogNauAn.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request
            , HttpServletResponse response
            , AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        Map<String, String> body = Map.of("message", "Bạn không đủ quyền truy cập chức năng này!");
        String json = new ObjectMapper().writeValueAsString(body);//Dùng jackson để nó convert string sang json cho
        response.getWriter().write(json);
    }
}

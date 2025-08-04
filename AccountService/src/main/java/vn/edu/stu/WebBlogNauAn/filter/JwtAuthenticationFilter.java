package vn.edu.stu.WebBlogNauAn.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import vn.edu.stu.WebBlogNauAn.dto.MyPrincipal;
import vn.edu.stu.WebBlogNauAn.utils.JWTUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JWTUtils jwtUtils;
    private final AntPathMatcher matcher = new AntPathMatcher();

    /** Các URL không cần xác thực */
    private static final Set<String> PUBLIC_URLS = Set.of(
            "/api/accounts/auth/login",
            "/api/accounts/auth/register",
            "/api/accounts/auth/refresh-access-token",
            "/api/accounts/auth/verify",
            "/api/accounts/messages/community"
            );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isPublic(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        /* Lấy token: ưu tiên header, sau đó cookie */
        String token = resolveToken(request);
        if (token == null) { 
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        /*Validate */
        String email = jwtUtils.extractEmail(token);
        if (email == null || !jwtUtils.validateToken(token, email)) { // Token sai/hết hạn ⇒ 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        /*Đặt Authentication */
        String role = jwtUtils.extractRole(token);
        long accountId = jwtUtils.extractAccountId(token);

        UsernamePasswordAuthenticationToken auth = buildAuthentication(email, role, accountId);

        SecurityContextHolder.getContext().setAuthentication(auth);

        /* Cho request đi tiếp */
        filterChain.doFilter(request, response);
    }

    /* Helpers ---------------------------------------------------------------- */

    /** Xác định token từ header hoặc cookie */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        Cookie cookie = WebUtils.getCookie(request, "accessToken");
        return cookie != null ? cookie.getValue() : null;
    }

    /** Kiểm tra đường dẫn có thuộc các URL public hay không */
    private boolean isPublic(String uri) {
        return PUBLIC_URLS.stream().anyMatch(p -> matcher.match(p, uri));
    }

    /** Tạo UsernamePasswordAuthenticationToken */
    private static UsernamePasswordAuthenticationToken buildAuthentication(
            String email, String role, long accountId) {

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        MyPrincipal principal = new MyPrincipal();
        principal.setEmail(email);
        principal.setAccountId(accountId);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}

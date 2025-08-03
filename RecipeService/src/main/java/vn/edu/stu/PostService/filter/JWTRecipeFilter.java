package vn.edu.stu.PostService.filter;

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
import vn.edu.stu.PostService.dto.MyPrincipal;
import vn.edu.stu.PostService.utils.JWTUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JWTRecipeFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTRecipeFilter.class);
    private final JWTUtils jwtUtils;
    private final AntPathMatcher matcher = new AntPathMatcher();

    /* --------- URL công khai của recipe‑service ---------- */
    private static final Set<String> PUBLIC_GET = Set.of(
            "/api/recipes",
            "/api/recipes/search",
            "/api/recipes/categories/all");

    /* ========= FILTER ========== */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        /* 1. Bỏ qua nếu là GET vào các URL công khai */
        if (isPublicGet(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* 2. Lấy token */
        String token = resolveToken(request);
        if (token == null) { // không có token --> 403
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        /* 3. Validate */
        String email = jwtUtils.extractEmail(token);
        if (email == null || !jwtUtils.isTokenValid(token, email)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 token sai / hết hạn
            return;
        }

        /* 4. Xây Authentication */
        String role = jwtUtils.extractRole(token);
        long accountId = jwtUtils.getAccount_id(token);

        UsernamePasswordAuthenticationToken auth = buildAuthToken(role, email, accountId);

        SecurityContextHolder.getContext().setAuthentication(auth);

        /* 5. Cho request đi tiếp */
        filterChain.doFilter(request, response);
    }

    /* --------- Helpers ---------- */

    /** true nếu request là GET và path khớp danh sách public */
    private boolean isPublicGet(HttpServletRequest req) {
        if (!"GET".equalsIgnoreCase(req.getMethod()))
            return false;
        String path = req.getRequestURI();
        return PUBLIC_GET.stream().anyMatch(p -> matcher.match(p, path));
    }

    /** Ưu tiên header, sau đó cookie accessToken */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        Cookie cookie = WebUtils.getCookie(request, "accessToken");
        return cookie != null ? cookie.getValue() : null;
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(String role,
            String email,
            long accountId) {

        List<GrantedAuthority> auth = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        MyPrincipal principal = new MyPrincipal(email, accountId);
        return new UsernamePasswordAuthenticationToken(principal, null, auth);
    }
}

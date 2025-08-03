package vn.edu.stu.WebBlogNauAn.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    // ✅ Đọc cookie theo key
    public String getItemFromCookies(String key, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // ✅ Thêm refresh token vào cookie
    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(false).path("/").maxAge(3 * 24 * 60 * 60).sameSite("Lax") //cho no song 3 ngay
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // ✅ Cập nhật refresh token (xóa rồi thêm lại)
    public void updateRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        deleteRefreshTokenFromCookie(response);
        addRefreshTokenToCookie(response, refreshToken);
    }

    // ✅ Xóa refresh token bằng cookie rỗng
    public void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

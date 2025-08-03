package vn.edu.stu.WebBlogNauAn.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.JwtException;
import vn.edu.stu.WebBlogNauAn.dto.LoginDto;
import vn.edu.stu.WebBlogNauAn.dto.RegisterDto;
import vn.edu.stu.WebBlogNauAn.exception.UnauthorizedException;
import vn.edu.stu.WebBlogNauAn.response.ApiResponse;
import vn.edu.stu.WebBlogNauAn.service.AccountService;
import vn.edu.stu.WebBlogNauAn.service.RedisService;
import vn.edu.stu.WebBlogNauAn.utils.JWTUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value = "/api/accounts/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JWTUtils jwtUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountService accountService;
    private final RedisService redisService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginDto authRequest, HttpServletRequest request,
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) String oldRefreshToken) {
        return accountService.emailLogin(authRequest.getEmail(), authRequest.getPassword(), request, response,
                oldRefreshToken);
    }

    @PostMapping(value = "/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody RegisterDto registerRequest, HttpServletResponse response) {
        registerRequest.setUsername(registerRequest.getUsername());
        registerRequest.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));

        return accountService.register(registerRequest);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) throws IOException {
        try {
            // Lấy email từ token
            String email = jwtUtils.extractEmail(token);
            // Kiểm tra token hết hạn chưa
            if (jwtUtils.isTokenExpired(token)) {
                model.addAttribute("status", false);
                model.addAttribute("message", "Thời gian xác thực đã hết, vui lòng đăng ký lại!");
                return "verify-email-result"; // Trả về thymeleaf
            }
            // Lấy thông tin đăng ký tạm từ Redis
            RegisterDto tempRegister = redisService.getValue("temp:user:" + email, RegisterDto.class);
            if (tempRegister == null) {
                model.addAttribute("status", false);
                model.addAttribute("message", "Thông tin đăng ký không tồn tại hoặc đã hết hạn!");
                return "verify-email-result";
            }
            // Lưu tài khoản vào DB
            accountService.saveAccount(tempRegister);

            // Đánh dấu email đã được xác thực
            redisService.registerEmail(email);
            System.out.println("Key registered:" + email + " exists? " + redisService.isEmailRegisteredRedis(email));

            // Xoá Redis tạm
            redisService.deleteValue("temp:user:" + email);

            model.addAttribute("status", true);
            model.addAttribute("message", "Tài khoản đã được xác thực và đăng ký thành công!");
            return "verify-email-result";

        } catch (JwtException e) {
            model.addAttribute("status", false);
            model.addAttribute("message", "Token không hợp lệ.");
            return "verify-email-result";
        }
    }

    @PostMapping(value = "/refresh-access-token")
    @ResponseBody
    public ResponseEntity<?> accessToken(HttpServletRequest request, HttpServletResponse response,
            @CookieValue("refreshToken") String refreshToken) {
        logger.info("REFRESH TOKEN để làm mới ACCESS TOKEN: {}", refreshToken);
        if (refreshToken == null || jwtUtils.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Phiên làm việc hết hạn, vui lòng đăng nhập lại!");
        }
        String email = jwtUtils.extractEmail(refreshToken);
        String roleName = jwtUtils.extractRole(refreshToken);
        long userId = jwtUtils.extractAccountId(refreshToken);

        String newAccessToken = jwtUtils.generateAccessToken(email, userId, roleName);
        ResponseCookie cookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true).secure(false).path("/").maxAge(60 * 60).sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new ApiResponse("Đã cập nhật accessToken mới", 200));
    }

    @PostMapping("/logOut")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return accountService.logout(response);
    }

}

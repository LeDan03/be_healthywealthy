package vn.edu.stu.WebBlogNauAn.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.stu.WebBlogNauAn.constant.appConstants;
import vn.edu.stu.WebBlogNauAn.dto.AdminRegisterDto;
import vn.edu.stu.WebBlogNauAn.dto.RegisterDto;
import vn.edu.stu.WebBlogNauAn.dto.UpdateAvatarDto;
import vn.edu.stu.WebBlogNauAn.dto.UpdateUsernameDto;
import vn.edu.stu.WebBlogNauAn.exception.BadRequestException;
import vn.edu.stu.WebBlogNauAn.exception.ConflicException;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.mapper.AccountMapper;
import vn.edu.stu.WebBlogNauAn.mapper.FollowMapper;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.model.Follow;
import vn.edu.stu.WebBlogNauAn.model.Role;
import vn.edu.stu.WebBlogNauAn.repository.AccountRepo;
import vn.edu.stu.WebBlogNauAn.response.*;
import vn.edu.stu.WebBlogNauAn.utils.JWTUtils;

import jakarta.servlet.http.HttpServletRequest; // Sửa từ javax sang jakarta
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepo accountRepo;
    private final JWTUtils jwtUtils;
    private final AccountMapper accountMapper;
    private final RedisService redisService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CookieService cookieService;
    private final RoleService roleService;
    private final FollowMapper followMapper;
    private final MailService mailService;
    private final AccountIndexerService accountIndexerService;
    private final CloudinaryService cloudinaryService;

    private final AccountSearchService accountSearchService;

    private final String DEFAULT_AVARTAR_URL = appConstants.DEFAULT_AVARTAR_URL;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.server.ip}")
    private String serverIp;

    @PostConstruct
    public void initAdminAccount() {
        if (accountRepo.count() == 0) {
            Account admin = new Account();
            admin.setEmail("leyendanwork@gmail.com");
            admin.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            admin.setUsername("Healthy Wealthy");
            admin.setCreatedAt(Timestamp.from(Instant.now()));
            admin.setRole(roleService.findRoleByName("ADMIN"));
            admin.setStatus("active");
            accountRepo.save(admin);
            redisService.registerEmail(admin.getEmail());
        }
    }

    @PostConstruct
    public void initDefaultUser() throws IOException {
        int currentYear = Year.now().getValue();

        Account LeYenDan = new Account();
        Account PhanAnhDuong = new Account();
        Account BienCatTuong = new Account();
        Account BienCatTien = new Account();
        Account LeMinhHoang = new Account();

        LeYenDan.setUsername("Lê Yến Đan");
        LeYenDan.setEmail("leedan0123s@gmail.com");
        LeYenDan.setPassword(bCryptPasswordEncoder.encode("leyendan"));
        LeYenDan.setRole(roleService.findRoleByName("USER"));
        LeYenDan.setStatus("active");
        LeYenDan.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(currentYear, 1, 10, 10, 0))); // Tháng 1
        if (!accountRepo.existsByEmail(LeYenDan.getEmail())) {
            accountRepo.save(LeYenDan);
            redisService.registerEmail(LeYenDan.getEmail());
            accountIndexerService.indexAccount(LeYenDan);
        }

        PhanAnhDuong.setUsername("Phan Thị Ánh Dương");
        PhanAnhDuong.setEmail("phananhduong@gmail.com");
        PhanAnhDuong.setPassword(bCryptPasswordEncoder.encode("phananhduong"));
        PhanAnhDuong.setRole(roleService.findRoleByName("USER"));
        PhanAnhDuong.setStatus("active");
        PhanAnhDuong.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(currentYear, 2, 10, 10, 0))); // Tháng 2
        if (!accountRepo.existsByEmail(PhanAnhDuong.getEmail())) {
            accountRepo.save(PhanAnhDuong);
            redisService.registerEmail(PhanAnhDuong.getEmail());
            accountIndexerService.indexAccount(PhanAnhDuong);
        }

        BienCatTuong.setUsername("Biện Cát Tường");
        BienCatTuong.setEmail("biencattuong@gmail.com");
        BienCatTuong.setPassword(bCryptPasswordEncoder.encode("biencattuong"));
        BienCatTuong.setRole(roleService.findRoleByName("USER"));
        BienCatTuong.setStatus("active");
        BienCatTuong.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(currentYear, 2, 10, 12, 0))); // Tháng 2
        if (!accountRepo.existsByEmail(BienCatTuong.getEmail())) {
            accountRepo.save(BienCatTuong);
            redisService.registerEmail(BienCatTuong.getEmail());
            accountIndexerService.indexAccount(BienCatTuong);
        }

        BienCatTien.setUsername("Biện Cát Tiên");
        BienCatTien.setEmail("biencattien@gmail.com");
        BienCatTien.setPassword(bCryptPasswordEncoder.encode("biencattien"));
        BienCatTien.setRole(roleService.findRoleByName("USER"));
        BienCatTien.setStatus("active");
        BienCatTien.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(currentYear, 2, 10, 15, 0))); // Tháng 2
        if (!accountRepo.existsByEmail(BienCatTien.getEmail())) {
            accountRepo.save(BienCatTien);
            redisService.registerEmail(BienCatTien.getEmail());
            accountIndexerService.indexAccount(BienCatTien);
        }

        LeMinhHoang.setUsername("Lê Minh Hoàng");
        LeMinhHoang.setEmail("leminhhoang@gmail.com");
        LeMinhHoang.setPassword(bCryptPasswordEncoder.encode("leminhhoang"));
        LeMinhHoang.setRole(roleService.findRoleByName("USER"));
        LeMinhHoang.setStatus("active");
        LeMinhHoang.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(currentYear, 5, 10, 10, 0))); // Tháng 5
        if (!accountRepo.existsByEmail(LeMinhHoang.getEmail())) {
            accountRepo.save(LeMinhHoang);
            redisService.registerEmail(LeMinhHoang.getEmail());
            accountIndexerService.indexAccount(LeMinhHoang);
        }
    }

    public void saveAccount(RegisterDto registerDto) throws IOException {
        Role role = roleService.findRoleByName("USER");

        Account account = accountMapper.toAccount(registerDto);
        account.setRole(role);
        account.setCreatedAt(Timestamp.from(Instant.now()));
        account.setStatus("active");
        account.setAvatarUrl(DEFAULT_AVARTAR_URL);
        accountRepo.save(account);
        accountIndexerService.indexAccount(account);
    }

    public void updateAvatar(UpdateAvatarDto updateAvatarDto, String email) {
        Account account = accountRepo.findByEmail(email).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }

        String oldAvatarPublicId = account.getAvatarPublicId();
        if (oldAvatarPublicId != null) {
            cloudinaryService.deleteImage(oldAvatarPublicId);
            logger.info("Da xoa avatar cu tren cloudinary voi public id:{}", oldAvatarPublicId);
        } else {
            logger.info(" New public id by dto:{}", updateAvatarDto.getPublicId());
        }
        accountRepo.updateAvatarUrlByEmail(email, updateAvatarDto.getAvatarUrl(), updateAvatarDto.getPublicId(),
                Timestamp.from(Instant.now()));
    }

    public void updateUsername(UpdateUsernameDto dto, String email) {
        Account account = accountRepo.findByEmail(email).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        accountRepo.updateUsernameByEmail(email, dto.getNewUsername(), Timestamp.from(Instant.now()));
    }

    public AccountResponse findAccountByEmail(String email) {
        return accountMapper.toAccountResponse(accountRepo.findByEmail(email).orElse(null));
    }

    public AccountResponse findAccountById(long accountId) {
        return accountMapper.toAccountResponse(accountRepo.findById(accountId).orElse(null));
    }

    public List<AccountResponse> findByIds(List<Long> accountIds) {
        return accountMapper.toAccountResponseList(accountRepo.findByIdIn(accountIds));
    }

    public ResponseEntity<?> register(RegisterDto registerDto) {
        String email = registerDto.getEmail();
        // Nếu email đã xác thực trước đó thì từ chối
        if (redisService.isEmailRegisteredRedis(email)) {
            throw new ConflicException("Email đã được đăng ký rồi!");
        }
        // Lưu RegisterDto tạm thời vào Redis với TTL 15 phút
        redisService.setValue("temp:user:" + email, registerDto, 15 * 60);

        // Tạo token xác thực
        String token = jwtUtils.generateEmailVerificationToken(email);
        // Gửi email xác thực
        String verifyUrl = String.format("http://%s:8080/api/accounts/auth/verify?token=%s", serverIp, token);
        mailService.sendVerificationEmail(email, verifyUrl);

        return ResponseEntity.ok(new ApiResponse("Vui lòng kiểm tra email để xác thực tài khoản.", 200));
    }

    public ResponseEntity<ApiResponse> emailLogin(
            String email, String password,
            HttpServletRequest request,
            HttpServletResponse response,
            String oldRefreshToken) {
        Account account;
        if (!redisService.isEmailRegisteredRedis(email)) {
            // throw new BadRequestException("Email chưa được đăng ký!");
            account = accountRepo.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("Email chưa được đăng ký!"));
        }

        account = accountRepo.findByEmail(email).get();

        if ("disable".equals(account.getStatus())) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu!");
        }
        if (!bCryptPasswordEncoder.matches(password, account.getPassword())) {
            throw new BadRequestException("Mật khẩu không đúng!");
        }

        long userId = account.getId();
        String role = account.getRole().getName();
        String emailFromUser = account.getEmail();

        String refreshToken;
        if (oldRefreshToken == null || jwtUtils.isTokenExpired(oldRefreshToken)) {
            refreshToken = jwtUtils.generateRefreshToken(emailFromUser, userId, role);
            cookieService.addRefreshTokenToCookie(response, refreshToken);
        } else {
            refreshToken = oldRefreshToken;
        }

        String accessToken = jwtUtils.generateAccessToken(emailFromUser, userId, role);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // Bật lên true khi deploy
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.ok(new ApiResponse("Đăng nhập thành công!", 200));
    }

    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        ResponseCookie accessTokenClear = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) // nên bật true ở production
                .path("/")
                .maxAge(0) // xóa cookie
                .sameSite("Lax")
                .build();

        // Xóa refreshToken
        ResponseCookie refreshTokenClear = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenClear.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenClear.toString());

        return ResponseEntity.ok(new ApiResponse("Đã đăng xuất!", 200));
    }

    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<Account> accounts = accountRepo.findAll();
        return ResponseEntity.ok().body(accountMapper.toAccountResponseList(accounts));// chuyen entity sang dto
    }

    public ResponseEntity<ApiResponse> togggleAccountStatus(long id) {
        Optional<Account> accountOptional = accountRepo.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            String oldStatus = account.getStatus();
            String newStatus;
            if (oldStatus.equals("active")) {
                newStatus = "disable";
            } else {
                newStatus = "active";
            }
            account.setStatus(newStatus);
            accountRepo.save(account);
            return ResponseEntity.ok().body(new ApiResponse("Đã thay đổi trạng thái", 200));
        } else {
            throw new ResourceNotFoundException("Tài khoản này không tồn tại");
        }
    }

    public ResponseEntity<AccountResponse> adminCreateAccount(AdminRegisterDto adminRegisterDto) {
        if (redisService.isEmailRegistered(adminRegisterDto.getEmail())) {
            throw new BadRequestException("Email này đã được đăng ký!");
        }
        Role role = roleService.findRoleByName(adminRegisterDto.getRole());
        if (role == null) {
            throw new ResourceNotFoundException("Role này không tồn tai!");
        }
        Account account = accountMapper.toAccount(adminRegisterDto);
        account.setCreatedAt(Timestamp.from(Instant.now()));
        account.setRole(role);
        accountRepo.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.toAccountResponse(account));
    }

    public List<AccountResponse> getAllFollowees(long accountId) {
        Optional<Account> accountOpt = accountRepo.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại");
        }
        Account account = accountOpt.get();
        List<Follow> follows = account.getFollowing();
        List<Account> followees = followMapper.followeesToAccounts(follows);
        return accountMapper.toAccountResponseList(followees);
    }

    public List<AccountResponse> getAllFollowers(long accountId) {
        Optional<Account> accountOpt = accountRepo.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại");
        }
        Account account = accountOpt.get();
        List<Follow> follows = account.getFollowers();
        List<Account> followers = followMapper.followersToAccounts(follows);
        return accountMapper.toAccountResponseList(followers);
    }

    // search
    public AccountResponse findByEmail(String email) {
        Optional<Account> opt = accountRepo.findByEmail(email);
        if (!opt.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }
        Account account = opt.get();
        return accountMapper.toAccountResponse(account);
    }

    public List<AccountResponse> findByUsername(String username) {
        try {
            List<Long> ids = accountSearchService.searchAccountByUsername(username);
            List<Account> accounts = accountRepo.findByIdIn(ids);

            return accountMapper.toAccountResponseList(accounts);
        } catch (Exception e) {
            // TODO: handle exception
            throw new ResourceNotFoundException("Không tìm thấy tài khoản nào!");
        }
    }
}

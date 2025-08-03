package vn.edu.stu.WebBlogNauAn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public void sendVerificationEmail(String email, String verifyUrl) {
        String subject = "Xác thực tài khoản Healthy Wealthy";
        String content = """
                Vui lòng nhấn vào link dưới đây để xác thực tài khoản: %s

                Link xác thực này sẽ hết hạn sau 15 phút
                """.formatted(verifyUrl, verifyUrl);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(message);
            log.info("DA GUI EMAIL TOI: " + email);
        } catch (Exception e) {
            log.info("KHONG THE GUI MAIL: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email");
        }
    }
}

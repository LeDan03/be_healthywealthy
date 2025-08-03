package vn.edu.stu.WebBlogNauAn.response;

import lombok.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterResponse {
    private String username;
    private String email;
    private Timestamp registerAt;
    private String status;
}

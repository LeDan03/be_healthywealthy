package vn.edu.stu.WebBlogNauAn.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}

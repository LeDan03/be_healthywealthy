package vn.edu.stu.WebBlogNauAn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRegisterDto {
    private String username;
    private String password;
    private String email;
    private String role;
}

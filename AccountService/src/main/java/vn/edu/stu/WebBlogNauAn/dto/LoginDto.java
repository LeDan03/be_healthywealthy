package vn.edu.stu.WebBlogNauAn.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginDto {
    private String email;
    private String password;

    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
}

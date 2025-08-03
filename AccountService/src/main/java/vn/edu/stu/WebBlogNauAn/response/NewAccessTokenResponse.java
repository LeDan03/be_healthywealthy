package vn.edu.stu.WebBlogNauAn.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewAccessTokenResponse {
    private String accessToken;
}

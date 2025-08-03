package vn.edu.stu.WebBlogNauAn.dto;

import lombok.*;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateAvatarDto {
    private String avatarUrl;
    private String publicId;
}

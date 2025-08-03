package vn.edu.stu.WebBlogNauAn.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class CommenterDto {
    private long id;
    private String username;
    private String avatarUrl;

    // De JPA map, JPA khong tu map bang AllArg cua lombok duoc
     public CommenterDto(long id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
}
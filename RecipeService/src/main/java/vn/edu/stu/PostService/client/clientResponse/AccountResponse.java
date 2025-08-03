package vn.edu.stu.PostService.client.clientResponse;

import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountResponse {
    private long id;
    private String username;
    private String email;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
}

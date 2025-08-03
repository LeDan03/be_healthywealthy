package vn.edu.stu.ApiGateway.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private long id;
    private String username;
    private String email;
    private String avatar_url;
    private Timestamp created_at;
    private Timestamp update_at;
    private String status;
}

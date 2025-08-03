package vn.edu.stu.common_dto.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeeklyAccountResponse {
    private long id;

    private long accountId;
    private String username;
    private String email;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp  updatedAt;
    private String status;
    private String role;

    private int rank;
    private int totalLoveAction;
}

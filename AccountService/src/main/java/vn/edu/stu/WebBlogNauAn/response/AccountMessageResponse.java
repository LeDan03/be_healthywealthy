package vn.edu.stu.WebBlogNauAn.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountMessageResponse {
    private long id;
    private String title;
    private String content;
    private Timestamp createdAt;
    private boolean read; // người dùng đã đọc chưa

    private long accountId;
    private long messageId;
}

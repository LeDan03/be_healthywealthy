package vn.edu.stu.WebBlogNauAn.response;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageResponse {
    private long id;
    private String title;
    private String content;
    private Timestamp createdAt;
    
    private int reasonId;

    private boolean sentToAll;

    private Boolean pinned;
}

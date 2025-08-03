package vn.edu.stu.WebBlogNauAn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountMessageRequest {
    private long accountId;
    private long messageId;
    private long recipeId;
}

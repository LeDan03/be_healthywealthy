package vn.edu.stu.WebBlogNauAn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageRequest {
    private String title;
    private String content;
    private int reasonId;
    private Long recipeId;
}

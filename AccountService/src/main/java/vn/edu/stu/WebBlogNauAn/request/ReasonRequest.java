package vn.edu.stu.WebBlogNauAn.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReasonRequest {
    private String content;
    private String relatedEntityType;
}

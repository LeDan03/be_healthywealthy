package vn.edu.stu.WebBlogNauAn.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReasonResponse {
    private int id;
    private String message; 
    private String relatedEntityType;
}

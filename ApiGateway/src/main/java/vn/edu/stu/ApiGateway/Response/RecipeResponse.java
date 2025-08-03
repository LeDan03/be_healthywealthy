package vn.edu.stu.ApiGateway.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {
    private int id;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private long authorId;
    private String categoryName;
    private List<String> imagesUrl;
}

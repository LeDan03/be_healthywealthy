package vn.edu.stu.PostService.response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.stu.PostService.dto.CommenterDto;

//giúp Jackson nhận biết nếu đã serialize một object có cùng id, nó sẽ không serialize toàn bộ lại (tránh loop vô hạn).
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommentResponse {
    private long id;
    private long recipeId;
    private long accountId;
    private Timestamp createdAt;
    private String content;
    private String imageUrl;
    private Long parentId;
    private CommenterDto commenter;

    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();

}

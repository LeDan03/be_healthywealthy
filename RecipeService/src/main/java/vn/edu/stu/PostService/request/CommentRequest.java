package vn.edu.stu.PostService.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.stu.PostService.dto.UpdateImageDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentRequest {
    private long accountId;
    private String content;
    private UpdateImageDto imageDto;
    private long parentId;
}

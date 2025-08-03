package vn.edu.stu.PostService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StepRequest {
    private int stt;
    private String content;
    private String imageUrl;
    private String imagePublicId;
}

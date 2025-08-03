package vn.edu.stu.WebBlogNauAn.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StepResponse {
    private int stt;
    private String content;
    private String imageUrl;
}

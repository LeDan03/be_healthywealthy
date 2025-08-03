package vn.edu.stu.PostService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.stu.PostService.dto.UpdateImageDto;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class UpdateRecipeRequest {
    private String title;
    private String content;
    private int categoryId;
    private List<UpdateImageDto> images;
    private List<StepRequest> steps;
}

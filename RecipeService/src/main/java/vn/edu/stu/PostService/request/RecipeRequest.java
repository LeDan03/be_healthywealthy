package vn.edu.stu.PostService.request;

import lombok.*;
import vn.edu.stu.PostService.dto.UpdateImageDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {
    private String title;
    private String content;
    private long accountId;
    private int categoryId;

    private List<UpdateImageDto> imageDtos;

    private int difficultyId;
    private int cookingTime;
    private int prepTime;
    private int servings;
    private List<IngredientRequest> ingredients;

    private List<StepRequest> steps;
    private boolean requirePublic;

    private boolean requireFeatured;
}

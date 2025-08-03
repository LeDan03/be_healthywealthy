package vn.edu.stu.PostService.response;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {
    private long id;
    private String title;
    private String content;
    private List<String> imagesUrl;
    private long authorId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String categoryName;
    private int difficultyId;
    private int prepTime;
    private int cookTime;
    private int servings;
    private long loveCount;
    private long saveCount;
    private List<StepResponse> steps;
    private List<IngredientResponse> ingredients;
    private boolean published;
    private boolean featured;
}

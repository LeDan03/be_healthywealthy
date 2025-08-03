package vn.edu.stu.PostService.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipesPageableResponse {
    private List<RecipeResponse> recipeResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}

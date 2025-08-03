package vn.edu.stu.PostService.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.PostService.exception.NotFoundException;
import vn.edu.stu.PostService.model.Category;
import vn.edu.stu.PostService.model.Difficulty;
import vn.edu.stu.PostService.model.Image;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.repository.CategoryRepo;
import vn.edu.stu.PostService.repository.DifficultyRepo;
import vn.edu.stu.PostService.request.RecipeRequest;
import vn.edu.stu.PostService.response.RecipeResponse;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final CategoryRepo categoryRepo;
    private final StepMapper stepMapper;
    private final DifficultyRepo difficultyRepo;
    private final IngredientMapper ingredientMapper;
    private final ImageMapper imageMapper;

    public Recipe requestToRecipe(RecipeRequest recipeRequest) {
        Category category = categoryRepo.findById(recipeRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category does not existed"));
        Difficulty difficulty = difficultyRepo.findById(recipeRequest.getDifficultyId()).orElseThrow(
                () -> new NotFoundException("Difficulty not found with ID: " + recipeRequest.getDifficultyId()));

        Recipe recipe = new Recipe();
        recipe.setTitle(recipeRequest.getTitle());
        recipe.setContent(recipeRequest.getContent());
        recipe.setAccountId(recipeRequest.getAccountId());
        recipe.setCategory(category);
        recipe.setDifficulty(difficulty);
        recipe.setCreatedAt(Timestamp.from(Instant.now()));
        recipe.setPrepTime(recipeRequest.getPrepTime());
        recipe.setCookTime(recipeRequest.getCookingTime());
        recipe.setServings(recipeRequest.getServings());
        recipe.setIngredients(ingredientMapper.toIngredientList(recipeRequest.getIngredients(), recipe));
        recipe.setFeatured(recipeRequest.isRequireFeatured());
        if (!recipeRequest.getImageDtos().isEmpty()) {
            recipe.setImagesUrl(imageMapper.toImageList(recipeRequest.getImageDtos(), recipe));
        }
        recipe.setSteps(stepMapper.toSteps(recipeRequest.getSteps(), recipe));
        recipe.setPublished(recipeRequest.isRequirePublic());
        return recipe;
    }

    public RecipeResponse toResponse(Recipe recipe) {
        // log.info("STEP 1: ", recipe.getSteps().get(0).getContent());
        return RecipeResponse.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .content(recipe.getContent())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdateAt())
                .imagesUrl(recipe.getImagesUrl() != null ? recipe.getImagesUrl().stream()
                        .filter(Objects::nonNull)
                        .map(Image::getUrl)
                        .toList()
                        : List.of())
                .steps(recipe.getSteps() != null
                        ? stepMapper.toResponses(recipe.getSteps())
                        : List.of())
                .authorId(recipe.getAccountId())
                .difficultyId(recipe.getDifficulty().getId())
                .loveCount(recipe.getLoveCount())
                .saveCount(recipe.getSaveCount())
                .prepTime(recipe.getPrepTime())
                .cookTime(recipe.getCookTime())
                .servings(recipe.getServings())
                .categoryName(recipe.getCategory() != null ? recipe.getCategory().getName() : null)
                .ingredients(ingredientMapper.toIngredients(recipe.getIngredients()))
                .published(recipe.isPublished())
                .featured(recipe.isFeatured())
                .build();
    }

    public List<RecipeResponse> toResponseList(List<Recipe> recipes) {
        return recipes.stream().map(this::toResponse).collect(Collectors.toList());
    }
}

package vn.edu.stu.PostService.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.model.Category;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.response.CategoryResponse;
import vn.edu.stu.PostService.response.RecipeResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .recipeIds(category.getRecipes() != null ? category.getRecipes().stream().map(Recipe::getId).toList()
                        : List.of())
                .build();
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

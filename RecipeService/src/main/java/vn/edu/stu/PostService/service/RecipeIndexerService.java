package vn.edu.stu.PostService.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import vn.edu.stu.PostService.dto.RecipeDocumentDto;
import vn.edu.stu.PostService.model.Recipe;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RecipeIndexerService {

    private final ElasticsearchClient elasticsearchClient;

    public void indexRecipe(Recipe recipe) throws IOException {
        // Convert Recipe to RecipeResponse
        RecipeDocumentDto recipeDocumentDto = RecipeDocumentDto.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .content(recipe.getContent())
                .difficulty(RecipeDocumentDto.DifficultyDocument.builder()
                        .name(recipe.getDifficulty() != null ? recipe.getDifficulty().getName() : null)
                        .build())
                .category(RecipeDocumentDto.CategoryDocument.builder()
                        .name(recipe.getCategory().getName()).build())
                .build();

        // Tạo IndexRequest sử dụng RecipeResponse
        IndexRequest<RecipeDocumentDto> request = new IndexRequest.Builder<RecipeDocumentDto>()
                .index("recipes")
                .id(String.valueOf(recipe.getId()))
                .document(recipeDocumentDto)
                .refresh(Refresh.True)
                .build();

        elasticsearchClient.index(request);
    }
}

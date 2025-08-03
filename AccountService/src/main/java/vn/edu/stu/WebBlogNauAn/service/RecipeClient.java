package vn.edu.stu.WebBlogNauAn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.stu.WebBlogNauAn.response.RecipeResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeClient {

    private final WebClient.Builder clientBuilder;

    public List<RecipeResponse> getRecipesByAccountIds(List<Long> accountIds, String accessToken) {
        return clientBuilder.build()
                .post()
                .uri("lb://recipe-service/api/recipes/by-account-ids")
                .bodyValue(accountIds)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RecipeResponse>>() {
                })
                .block();
    }

    public List<RecipeResponse> getRecipesByIds(List<Long> recipeIds, String accessToken) {
        return clientBuilder.build()
                .post()
                .uri("lb://recipe-service/api/recipes/by-ids")
                .bodyValue(recipeIds)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RecipeResponse>>() {
                })
                .block();
    }

}

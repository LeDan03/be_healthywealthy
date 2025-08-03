package vn.edu.stu.PostService.producer;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.common_dto.dto.CreateRecipeEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String CREATE_RECIPE_TOPIC = "create-recipe";

    public void sendCreateRecipeEvent(long accountId, long recipeId) {
        CreateRecipeEvent event = new CreateRecipeEvent(recipeId, accountId);
        kafkaTemplate.send(CREATE_RECIPE_TOPIC, event);
    }

    public void sendDeleteRecipeEvent(long recipeId) {
        kafkaTemplate.send("delete-recipe", String.valueOf(recipeId));
    }
}

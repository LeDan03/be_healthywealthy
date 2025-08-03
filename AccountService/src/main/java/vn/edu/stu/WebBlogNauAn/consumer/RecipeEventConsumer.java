package vn.edu.stu.WebBlogNauAn.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.service.InteractionService;
import vn.edu.stu.WebBlogNauAn.service.RedisService;
import vn.edu.stu.common_dto.dto.CreateRecipeEvent;

@Component
@RequiredArgsConstructor
public class RecipeEventConsumer {
    private final RedisService redisService;
    private final InteractionService interactionService;
    private static final String DEFAULT_CONTAINER_FACTORY = "kafkaListenerContainerFactory";

    @KafkaListener(topics = "create-recipe", groupId = "account-recipe-public-group", containerFactory = DEFAULT_CONTAINER_FACTORY)
    public void listenCreateRecipeEvent(CreateRecipeEvent event) {
        redisService.pushNewPendingApproval(event);
    }

    @KafkaListener(topics = "delete-recipe", groupId = "account-recipe-delete-group", containerFactory = DEFAULT_CONTAINER_FACTORY)
    public void listenDeleteRecipeEvent(String recipeIdStr) {
        try {
            long recipeId = Long.parseLong(recipeIdStr);
            interactionService.deleteRecipeInteraction(recipeId);
           
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý xóa recipe interaction");
        }
    }

}

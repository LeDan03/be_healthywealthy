package vn.edu.stu.PostService.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.repository.RecipeRepo;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final RecipeRepo recipeRepo;

    @KafkaListener(topics = "reject-recipe-events", groupId ="notification-group", containerFactory="kafkaListenerContainerFactory")
    public void listenRejectEvent(String recipeId){
        recipeRepo.updatePublishedFalseById(Long.parseLong(recipeId));
    }
}

package vn.edu.stu.WebBlogNauAn.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final static String REJECT_RECIPE_TOPIC = "reject-recipe-events";

    public void sendRejectRecipeEvent(long recipeId) {
        kafkaTemplate.send(REJECT_RECIPE_TOPIC, String.valueOf(recipeId));
        System.out.print("Reject recipe ID: " + recipeId);
    }
}

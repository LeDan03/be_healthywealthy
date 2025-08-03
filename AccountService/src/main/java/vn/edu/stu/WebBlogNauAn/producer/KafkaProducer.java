package vn.edu.stu.WebBlogNauAn.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private static final String TOPIC = "account-save-recipe-events"; //Topic gửi thông điệp
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        System.out.println("Sending message to Kafka: " + message);  // Log để kiểm tra thông điệp
        kafkaTemplate.send(TOPIC, message);  // Gửi thông điệp vào Kafka Topic
    }

    public void sendCheckRecipeExistsEvent(long recipeId) {
        System.out.println("Sending check recipe event to Kafka: " + recipeId);
        kafkaTemplate.send("Check-recipe-exists", String.valueOf(recipeId));
    }


}

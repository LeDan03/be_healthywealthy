package vn.edu.stu.WebBlogNauAn.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.stu.WebBlogNauAn.holder.RecipeHolder;

@Component
public class KafkaConsumer {
    private final RecipeHolder recipeHolder;

    @Autowired
    public KafkaConsumer(RecipeHolder recipeHolder) {
        this.recipeHolder = recipeHolder;
    }

    @KafkaListener(topics = "Recipe-exists-response", groupId = "account-group")
    public void listen(String message) {
        System.out.println("Recipe phản hồi tới account: " + message);
        recipeHolder.putResponse(message);
    }
}

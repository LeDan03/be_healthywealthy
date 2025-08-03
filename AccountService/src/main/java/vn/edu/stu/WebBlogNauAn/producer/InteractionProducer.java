package vn.edu.stu.WebBlogNauAn.producer;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.common_dto.dto.InteractionType;
import vn.edu.stu.common_dto.dto.LoveEvent;
import vn.edu.stu.common_dto.dto.SaveEvent;

// import vn.edu.stu.WebBlogNauAn.dto.LoveEvent;

@Component
@RequiredArgsConstructor
public class InteractionProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String LOVETOPIC = "love-events";
    private static final String SAVETOPIC = "save-events";
    private static final String REQUIRE_FEATURE_TOPIC = "require-featured-events";


    public String getEventKey(long accountId, long recipeId, InteractionType type){
        return accountId+"-"+recipeId+"-"+type;
    }

    public void sendLoveEvent(long recipeId, long accountId) {
        LoveEvent loveEvent = LoveEvent.builder()
        .loveEventId(UUID.randomUUID().toString())
        .recipeId(recipeId)
        .accountId(accountId)
        .type(InteractionType.LOVE)
        .build();
        kafkaTemplate.send(LOVETOPIC,loveEvent);
    }

    public void sendUnloveEvent(long recipeId, long accountId){
        LoveEvent loveEvent = LoveEvent.builder()
        .loveEventId(UUID.randomUUID().toString())
        .recipeId(recipeId)
        .accountId(accountId)
        .type(InteractionType.UNLOVE)
        .build();
        kafkaTemplate.send(LOVETOPIC,loveEvent);
    }

    public void sendSaveEvent(long recipeId, long accountId){
        SaveEvent saveEvent = SaveEvent.builder()
        .saveEventId(UUID.randomUUID().toString())
        .recipeId(recipeId)
        .accountId(accountId)
        .type(InteractionType.SAVE)
        .build();
        kafkaTemplate.send(SAVETOPIC, saveEvent);
    }
    public void sendUnsaveEvent(long recipeId, long accountId){
        SaveEvent saveEvent = SaveEvent.builder()
        .saveEventId(UUID.randomUUID().toString())
        .recipeId(recipeId)
        .accountId(accountId)
        .type(InteractionType.UNSAVE)
        .build();
        kafkaTemplate.send(SAVETOPIC, saveEvent);
    }

    public void sendRequireFeaturedRecipeIds(List<Long> recipeIds){
        kafkaTemplate.send(REQUIRE_FEATURE_TOPIC,recipeIds);
    }
}

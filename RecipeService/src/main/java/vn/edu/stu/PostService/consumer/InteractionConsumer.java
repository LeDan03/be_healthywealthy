package vn.edu.stu.PostService.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.edu.stu.PostService.repository.RecipeRepo;
import vn.edu.stu.PostService.service.RecipeService;
import vn.edu.stu.PostService.service.RedisService;
import vn.edu.stu.common_dto.dto.LoveEvent;
import vn.edu.stu.common_dto.dto.SaveEvent;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InteractionConsumer {

    private final RecipeService recipeService;
    private final RecipeRepo recipeRepo;
    private static final String PROCESSED_EVENT_KEY_PREFIX = "processed-event:";
    private final RedisService redisService;

    @KafkaListener(topics = "love-events", groupId = "recipe-love-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenLoveEvent(LoveEvent event, Acknowledgment acknowledgment) {
        String eventKey = event.getAccountId() + "-" + event.getRecipeId() + "-" + event.getType();

        try {
            // if (isEventProcessed(eventKey)) {
            //     log.debug("↩ Bỏ qua event trùng {}", eventKey);
            //     acknowledgment.acknowledge();
            //     return;
            // }

            boolean processed = recipeService.applyLoveEvent(event);
            if (processed) {
                // markEventAsProcessed(eventKey);
                log.debug("✔ Đã xử lý event {}", eventKey);
            }
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Lỗi xử lý event {}", event, ex);
        }
    }

    private boolean isEventProcessed(String eventKey) {
        return Boolean.TRUE.equals(redisService.isExistedKey(PROCESSED_EVENT_KEY_PREFIX + eventKey));
    }

    private void markEventAsProcessed(String eventKey) {
        redisService.markEventAsProcessed(PROCESSED_EVENT_KEY_PREFIX, eventKey);
    }

    @KafkaListener(topics = "save-events", groupId = "recipe-save-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenSaveEvent(SaveEvent event, Acknowledgment acknowledgment) {
        String eventKey = event.getAccountId() + "-" + event.getRecipeId() + "-" + event.getType();

        try {
            // if (isEventProcessed(eventKey)) {
            //     log.debug("↩ Bỏ qua save event trùng {}", eventKey);
            //     acknowledgment.acknowledge();
            //     return;
            // }

            boolean processed = recipeService.applySaveEvent(event);
            if (processed) {
                // markEventAsProcessed(eventKey);
                log.debug("✔ Đã xử lý save event {}", eventKey);
            }

            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Lỗi xử lý save event {}", event, ex);
        }
    }

    @KafkaListener(topics = "require-featured-events", groupId = "recipe-save-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenRequiredFeaturedEvent(List<Long> recipeIds, Acknowledgment acknowledgment) {
        recipeRepo.updateFeaturedRecipes(recipeIds);
        acknowledgment.acknowledge();
    }
}

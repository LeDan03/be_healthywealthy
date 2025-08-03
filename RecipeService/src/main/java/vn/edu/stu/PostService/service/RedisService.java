package vn.edu.stu.PostService.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.model.Comment;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void plusOneLove(long recipeId) {
        redisTemplate.opsForHash().increment("recipe:loveCount", recipeId, 1);
    }

    public void minusOneLove(long recipeId) {
        redisTemplate.opsForHash().increment("recipe:loveCount", recipeId, -1);
    }


    public void plusOneSaved(long recipeId) {
        redisTemplate.opsForHash().increment("recipe:savedCount", recipeId, 1);
    }

    public void minusOneSaved(long recipeId) {
        redisTemplate.opsForHash().increment("recipe:savedCount", recipeId, -1);
    }

    // Xử lý comment bằng Redis List
    private String getCommentKey(long recipeId) {
        return "comments:" + recipeId;
    }

    public void pushCommentToCache(Comment comment) {
        try {
            String json = objectMapper.writeValueAsString(comment);
            String key = getCommentKey(comment.getRecipe().getId());
            // System.out.println("Pushing to Redis with key: " + key + ", JSON: " + json);
            redisTemplate.opsForList().rightPush(key, json);
            Long length = redisTemplate.opsForList().size(key);
            // System.out.println("Pushed to Redis, list length: " + length);
        } catch (JsonProcessingException e) {
            System.err.println("Lỗi chuyển comment sang JSON: " + e.getMessage());
            throw new RuntimeException("Serialization failed", e);
        } catch (Exception e) {
            System.err.println("Redis push error: " + e.getMessage());
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    public void removeCommentFromCache(long accountId, long recipeId) {
        List<String> all = redisTemplate.opsForList().range(getCommentKey(recipeId), 0, -1);
        if (all == null || all.isEmpty()) {
            return;
        }
        for (String json : all) {
            try {
                Comment comment = objectMapper.readValue(json, Comment.class);
                if (accountId == comment.getAccountId()) {
                    redisTemplate.opsForList().remove(getCommentKey(recipeId), 1, json);
                    break;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void markEventAsProcessed(String setKey,String eventKey) {
        redisTemplate.opsForValue().set(setKey + eventKey, "1", Duration.ofHours(24));
    }
    
    public boolean isExistedKey(String key){
        return redisTemplate.hasKey(key);
    }

    public Set<String> getItemsByKey(String key) {
        return redisTemplate.keys(key);
    }

    public List<String> getRawList(String key, int top, int bottom) {
        return redisTemplate.opsForList().range(key, top, bottom);
    }

    public void deleteAll(String key) {
        redisTemplate.delete(key);
    }
}

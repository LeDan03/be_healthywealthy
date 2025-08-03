
package vn.edu.stu.WebBlogNauAn.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.repository.LovedRecipeRepo;
import vn.edu.stu.WebBlogNauAn.repository.SavedRecipeRepo;
import vn.edu.stu.WebBlogNauAn.utils.TimeUtils;
import vn.edu.stu.common_dto.dto.CreateRecipeEvent;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LovedRecipeRepo lovedRecipeRepo;
    private final SavedRecipeRepo savedRecipeRepo;
    private final ObjectMapper objectMapper;

    private final TimeUtils timeUtils;

    public void saveToCache(String searchKey, String value) {
        redisTemplate.opsForValue().set(searchKey, value);
    }

    public Set<String> getFromCache(String searchKey) {
        return redisTemplate.opsForSet().members(searchKey);
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Set<Long> getRecipeIdsFromCache(String key) {
        Set<String> rawSet = redisTemplate.opsForSet().members(key);
        if (rawSet == null)
            return Set.of();
        return rawSet.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public long removeFromCache(String key, String value) {
        Long removed = redisTemplate.opsForSet().remove(key, value);
        return removed != null ? removed : 0;
    }

    public boolean isEmailRegistered(String email) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("emails", email));
    }

    private String getSaveKey(long accountId) {
        return "saved:" + accountId;
    }

    private String getUnsaveKey(long accountId) {
        return "unsaved:" + accountId;
    }

    public void saveRecipe(long accountId, long recipeId) {
        redisTemplate.opsForSet().add(getSaveKey(accountId), String.valueOf(recipeId));
        redisTemplate.expire(getSaveKey(accountId), Duration.ofDays(6));
    }

    public void unsaveRecipe(long accountId, long recipeId) {
        redisTemplate.opsForSet().remove(getSaveKey(accountId), String.valueOf(recipeId));
    }

    public void markUnsave(long accountId, long recipeId) {
        redisTemplate.opsForSet().add(getUnsaveKey(accountId), String.valueOf(recipeId));
        redisTemplate.expire(getUnsaveKey(accountId), Duration.ofHours(6));
    }

    public void removeUnsaveMark(long accountId, long recipeId) {
        redisTemplate.opsForSet().remove(getUnsaveKey(accountId), String.valueOf(recipeId));
    }

    public Set<String> getSavedRecipeIds(long accountId) {
        Set<String> result = redisTemplate.opsForSet().members(getSaveKey(accountId));
        return result != null ? result : Set.of();
    }

    public Set<String> getUnsavedRecipeIds(long accountId) {
        Set<String> result = redisTemplate.opsForSet().members(getUnsaveKey(accountId));
        return result != null ? result : Set.of();
    }

    public boolean isSavedRecipe(long accountId, long recipeId) {
        String key = getSaveKey(accountId);
        Boolean inRedis = redisTemplate.opsForSet().isMember(key, String.valueOf(recipeId));
        if (Boolean.TRUE.equals(inRedis))
            return true;
        return savedRecipeRepo.existsByAccountIdAndRecipeId(accountId, recipeId);
    }

    // follow
    private String getFollowKey(long followeeId) {
        return "follows:" + followeeId;
    }

    private String getUnfollowKey(long followeeId) {
        return "unfollows:" + followeeId;
    }

    // nguoi dang theo doi lam key, nguoi duoc theo doi lam value
    private String getFollowingKey(long followerId) {
        return "following:" + followerId;
    }

    // nguoi huy theo doi lam key, nguoi bi huy theo doi lam value
    private String getUnfollowingKey(long followerId) {
        return "unfollowing:" + followerId;
    }

    public void followAnAccount(long followeeId, long followerId) {
        redisTemplate.opsForSet().add(getFollowKey(followeeId), String.valueOf(followerId));
        redisTemplate.expire(getFollowKey(followeeId), Duration.ofHours(6));

        redisTemplate.opsForSet().add(getFollowingKey(followerId), String.valueOf(followeeId));
        redisTemplate.expire(getFollowingKey(followerId), Duration.ofHours(6));
    }

    public void unfollowAnAccount(long followeeId, long followerId) {
        redisTemplate.opsForSet().remove(getFollowKey(followeeId), String.valueOf(followerId));
        redisTemplate.opsForSet().remove(getFollowingKey(followerId), String.valueOf(followeeId));
    }

    public boolean isFollowedAccount(long followeeId, long followerId) {
        return Boolean.TRUE
                .equals(redisTemplate.opsForSet().isMember(getFollowKey(followeeId), String.valueOf(followerId)));
    }

    public void markUnfollow(long followeeId, long followerId) {
        redisTemplate.opsForSet().add(getUnfollowKey(followeeId), String.valueOf(followerId));
        redisTemplate.expire(getUnfollowKey(followeeId), Duration.ofHours(6));

        redisTemplate.opsForSet().add(getUnfollowingKey(followerId), String.valueOf(followeeId));
        redisTemplate.expire(getUnfollowingKey(followerId), Duration.ofHours(6));
    }

    public void removeUnfollowMark(long followeeId, long followerId) {
        redisTemplate.opsForSet().remove(getUnfollowKey(followeeId), String.valueOf(followerId));
        redisTemplate.opsForSet().remove(getUnfollowingKey(followerId), String.valueOf(followeeId));
    }

    public Set<String> getFollowerIdsInCache(long followeeId) {
        Set<String> result = redisTemplate.opsForSet().members(getFollowKey(followeeId));
        return result != null ? result : Set.of();
    }

    public Set<String> getUnfollowerIdsInCache(long followeeId) {
        Set<String> result = redisTemplate.opsForSet().members(getUnfollowKey(followeeId));
        return result != null ? result : Set.of();
    }

    public Set<String> getFolloweeIdsInCache(long followerId) {
        Set<String> result = redisTemplate.opsForSet().members(getFollowingKey(followerId));
        return result != null ? result : Set.of();
    }

    public Set<String> getUnfolloweeIdsInCache(long followerId) {
        Set<String> result = redisTemplate.opsForSet().members(getUnfollowingKey(followerId));
        return result != null ? result : Set.of();
    }

    // love
    // Chỉ đặt lại TTL nếu đó là lần khởi tạo
    // Tạo 2 set để phục vụ thêm việc flush featured Recipe
    private String getLoveKeyByAccount(long accountId) {
        return "loved:account:" + accountId;
    }

    private String getLoveKeyByRecipe(long recipeId) {
        return "loved:recipe:" + recipeId;
    }

    private String getUnloveKeyByAccount(long accountId) {
        return "unloved:account:" + accountId;
    }

    private String getUnloveKeyByRecipe(long recipeId) {
        return "unloved:recipe:" + recipeId;
    }

    public void loveRecipe(long accountId, long recipeId) {
        String accountKey = getLoveKeyByAccount(accountId);
        String recipeKey = getLoveKeyByRecipe(recipeId);

        // Hỗ trợ thống kê ben cái analytic service
        String allRecipeKey = "loved:recipe:all";
        String allAccountKey = "loved:account:all";

        redisTemplate.opsForSet().add(accountKey, String.valueOf(recipeId));
        redisTemplate.opsForSet().add(recipeKey, String.valueOf(accountId));

        redisTemplate.opsForSet().add(allRecipeKey, String.valueOf(recipeId));
        redisTemplate.opsForSet().add(allAccountKey, String.valueOf(accountId));
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(accountKey))) {
            redisTemplate.expire(accountKey, timeUtils.getDurationUntilThisSaturday2350());
        }
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(recipeKey))) {
            redisTemplate.expire(recipeKey, timeUtils.getDurationUntilThisSaturday2350());
        }
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(allAccountKey))) {
            redisTemplate.expire(allAccountKey, timeUtils.getDurationUntilThisSaturday2350());
        }
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(allRecipeKey))) {
            redisTemplate.expire(allRecipeKey, timeUtils.getDurationUntilThisSaturday2350());
        }
    }

    public void unloveRecipe(long accountId, long recipeId) {
        // Remove from loved
        redisTemplate.opsForSet().remove(getLoveKeyByAccount(accountId), String.valueOf(recipeId));
        redisTemplate.opsForSet().remove(getLoveKeyByRecipe(recipeId), String.valueOf(accountId));

        // Add to unloved
        String unloveAccountKey = getUnloveKeyByAccount(accountId);
        String unloveRecipeKey = getUnloveKeyByRecipe(recipeId);

        redisTemplate.opsForSet().add(unloveAccountKey, String.valueOf(recipeId));
        redisTemplate.opsForSet().add(unloveRecipeKey, String.valueOf(accountId));

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(unloveAccountKey))) {
            redisTemplate.expire(unloveAccountKey, timeUtils.getDurationUntilThisSaturday2350());
        }
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(unloveRecipeKey))) {
            redisTemplate.expire(unloveRecipeKey, timeUtils.getDurationUntilThisSaturday2350());
        }
    }

    public Set<String> getLovedRecipeIds(long accountId) {
        Set<String> result = redisTemplate.opsForSet().members(getLoveKeyByAccount(accountId));
        return result != null ? result : Set.of();
    }

    public Set<String> getUnlovedRecipeIds(long accountId) {
        Set<String> result = redisTemplate.opsForSet().members(getUnloveKeyByAccount(accountId));
        return result != null ? result : Set.of();
    }

    public Set<String> getLoverAccountIds(long recipeId) {
        Set<String> result = redisTemplate.opsForSet().members(getLoveKeyByRecipe(recipeId));
        return result != null ? result : Set.of();
    }

    public Set<String> getUnloverAccountIds(long recipeId) {
        Set<String> result = redisTemplate.opsForSet().members(getUnloveKeyByRecipe(recipeId));
        return result != null ? result : Set.of();
    }

    public boolean isLovedRecipe(long accountId, long recipeId) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(getLoveKeyByAccount(accountId), String.valueOf(recipeId)));
    }

    //Phục vụ flush không bị trùng lặp
    public Set<String> getFlushedRecipeIds(Long accountId) {
        return redisTemplate.opsForSet().members("flushed:loved:account:" + accountId);
    }

    public void addFlushedRecipe(Long accountId, Long recipeId) {
        redisTemplate.opsForSet().add("flushed:loved:account:" + accountId, String.valueOf(recipeId));
    }


    // Xây dựng hàng đợi recipe cần admin duyệt
    public void pushNewPendingApproval(CreateRecipeEvent event) {
        String key = "pending-approval";
        String fieldKey = String.valueOf(event.getRecipeId());

        Boolean exists = redisTemplate.opsForHash().hasKey(key, fieldKey);
        if (Boolean.TRUE.equals(exists)) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.opsForHash().put(key, fieldKey, json);
        } catch (Exception e) {
            // TODO: handle exceptionS
            System.out.println("PARSE PENDING APPROVAL recipe failed");
            throw new RuntimeException("Serialization failed");
        }
    }

    public void removeReviewedRecipe(long recipeId) {
        redisTemplate.opsForHash().delete("pending-approval", String.valueOf(recipeId));
    }

    public List<CreateRecipeEvent> getPendingApprovalEvents() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("pending-approval");
        List<CreateRecipeEvent> result = new ArrayList<>();
        for (Object value : entries.values()) {
            try {
                CreateRecipeEvent event = objectMapper.readValue(value.toString(), CreateRecipeEvent.class);
                result.add(event);
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("Failed to parse PENDING APPROVAL recipe");
            }
        }
        return result;
    }

    // register
    public void setValue(String key, Object value, long timeToLiveSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(timeToLiveSeconds));
        } catch (Exception e) {
            throw new RuntimeException("Parse json khi đăng ký thất bại", e);
        }
    }

    public <T> T getValue(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Lỗi parse JSON từ redis", e);
        }
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    public void registerEmail(String email) {
        redisTemplate.opsForValue().set("registered:" + email, "true");
    }

    public boolean isEmailRegisteredRedis(String email) {
        return redisTemplate.hasKey("registered:" + email);
    }
}
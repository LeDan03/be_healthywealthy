package vn.edu.stu.AnalyticsService.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.AnalyticsService.dto.WeeklyAccountDto;
import vn.edu.stu.AnalyticsService.dto.WeeklyRecipeDto;
import vn.edu.stu.AnalyticsService.model.WeeklyRecipe;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private String getLoveKeyByRecipe(long recipeId) {
        return "loved:recipe:" + recipeId;
    }

    private String getLoveKeyByAccount(long accountId) {
        return "loved:account:" + accountId;
    }

    public List<Long> getAllWeeklyLovedRecipeIds() {
        Set<String> rawIds = redisTemplate.opsForSet().members("loved:recipe:all");
        if (rawIds == null) {
            return List.of();
        }
        return rawIds.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<Long> getAllWeeklyLoveActionAccountIds() {
        Set<String> rawIds = redisTemplate.opsForSet().members("loved:account:all");
        if (rawIds == null) {
            return List.of();
        }
        return rawIds.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<Long> getLoverAccountIds(long recipeId) {
        Set<String> rawIds = redisTemplate.opsForSet().members(getLoveKeyByRecipe(recipeId));
        if (rawIds == null)
            return List.of();
        return rawIds.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<Long> getLovedRecipeIds(long accountId) {
        Set<String> rawIds = redisTemplate.opsForSet().members(getLoveKeyByAccount(accountId));
        if (rawIds == null)
            return List.of();
        return rawIds.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<WeeklyRecipeDto> getTop3WeeklyRecipe(List<Long> allRecipeIds) {
        Map<Long, Integer> recipeLoveCountMap = new HashMap<>();

        for (long recipeId : allRecipeIds) {
            String key = "loved:recipe:" + recipeId;
            Long count = redisTemplate.opsForSet().size(key);
            recipeLoveCountMap.put(recipeId, count != null ? count.intValue() : 0);
        }
        
        AtomicInteger rankCounter = new AtomicInteger(1);

        return recipeLoveCountMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // sort desc
                .limit(3)
                .map(entry -> WeeklyRecipeDto.builder()
                        .recipeId(entry.getKey())
                        .loveCount(entry.getValue())
                        .rank(rankCounter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    public List<WeeklyAccountDto> getTop3AccountsByLoveActionCount(List<Long> allAccountIds) {
        Map<Long, Integer> accountLoveActionMap = new HashMap<>(); //

        for (long accountId : allAccountIds) {
            String key = "loved:account:" + accountId;
            Long count = redisTemplate.opsForSet().size(key);
            accountLoveActionMap.put(accountId, count != null ? count.intValue() : 0);
        }

        AtomicInteger rankCounter = new AtomicInteger(1);

        return accountLoveActionMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(entry -> WeeklyAccountDto.builder()
                        .accountId(entry.getKey())
                        .totalLoveAction(entry.getValue())
                        .rank(rankCounter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    public String test() {
        redisTemplate.opsForValue().set("testKey", "Analytics service hello REDIS");
        return redisTemplate.opsForValue().get("testKey");
    }
}

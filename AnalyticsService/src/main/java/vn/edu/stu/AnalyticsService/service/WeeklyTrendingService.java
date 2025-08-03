package vn.edu.stu.AnalyticsService.service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.AnalyticsService.repository.WeeklyRecipeRepo;
import vn.edu.stu.AnalyticsService.repository.WeeklyTrendingRepo;
import vn.edu.stu.AnalyticsService.response.WeeklyTrendingResponse;
import vn.edu.stu.common_dto.dto.AccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyAccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyRecipeResponse;
import vn.edu.stu.AnalyticsService.client.AccountClient;
import vn.edu.stu.AnalyticsService.dto.WeeklyAccountDto;
import vn.edu.stu.AnalyticsService.dto.WeeklyRecipeDto;
import vn.edu.stu.AnalyticsService.mapper.WeeklyAccountMapper;
import vn.edu.stu.AnalyticsService.mapper.WeeklyRecipeMapper;
import vn.edu.stu.AnalyticsService.mapper.WeeklyTrendingMapper;
import vn.edu.stu.AnalyticsService.model.*;

@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyTrendingService {

    private final WeeklyTrendingRepo weeklyTrendingRepo;
    private final WeeklyRecipeRepo weeklyRecipeRepo;
    private final RedisService redisService;
    private final WeeklyRecipeMapper weeklyRecipeMapper;
    private final WeeklyAccountMapper weeklyAccountMapper;
    private final WeeklyTrendingMapper weeklyTrendingMapper;
    private final AccountClient accountClient;

    private static final Logger logger = LoggerFactory.getLogger(WeeklyTrendingService.class);

    public void createWeeklyTrendingRecipes() {
        WeeklyTrending weeklyTrending = new WeeklyTrending();

        LocalDate now = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.forLanguageTag("vi-VN"));

        int week = now.get(weekFields.weekOfWeekBasedYear());
        int year = now.getYear();

        weeklyTrending.setWeek(week);
        weeklyTrending.setYear(year);

        List<WeeklyAccount> weeklyAccounts = getWeeklyAccounts(weeklyTrending);
        List<WeeklyRecipe> weeklyRecipes = getWeeklyRecipes(weeklyTrending);

        // Set vào WeeklyTrending
        weeklyTrending.setWeeklyAccounts(weeklyAccounts);
        weeklyTrending.setWeeklyRecipes(weeklyRecipes);

        // Lưu vào DB
        weeklyTrendingRepo.save(weeklyTrending);

    }

    public List<WeeklyRecipe> getWeeklyRecipes(WeeklyTrending weeklyTrending) {
        List<Long> ids = redisService.getAllWeeklyLovedRecipeIds();
        List<WeeklyRecipeDto> weeklyRecipeDtos = redisService.getTop3WeeklyRecipe(ids);

        List<WeeklyRecipe> weeklyRecipes = weeklyRecipeMapper.toWeeklyRecipes(weeklyRecipeDtos, weeklyTrending);

        return weeklyRecipes;
    }

    public List<WeeklyAccount> getWeeklyAccounts(WeeklyTrending weeklyTrending) {
        List<Long> ids = redisService.getAllWeeklyLoveActionAccountIds();
        List<WeeklyAccountDto> weeklyAccountDtos = redisService.getTop3AccountsByLoveActionCount(ids);

        List<WeeklyAccount> weeklyAccounts = weeklyAccountMapper.tWeeklyAccounts(weeklyAccountDtos, weeklyTrending);

        return weeklyAccounts;
    }

    // ?: không quan tâm ngày. *: Mỗi tháng
    @Scheduled(cron = "0 0 23 ? * SAT", zone = "Asia/Ho_Chi_Minh")
    public void flushWeeklyTrending() {
        createWeeklyTrendingRecipes();
        logger.info("ĐÃ FLUSH WEEKLY TRENDING VÀO DB");
    }

    // Xử lý trả dữ liệu
    public WeeklyTrendingResponse getWeeklyTrending(int week, int year, String accessToken) {
        Optional<WeeklyTrending> weeklyTrendingOpt = weeklyTrendingRepo.findByWeekAndYear(week, year);

        if (weeklyTrendingOpt.isPresent()) {
            WeeklyTrending weeklyTrending = weeklyTrendingOpt.get();
            return weeklyTrendingMapper.toWeeklyTrendingResponse(weeklyTrending, accessToken);
        }

        // Nếu không có trong DB, dùng Redis:
        List<Long> recipeIds = redisService.getAllWeeklyLovedRecipeIds();
        List<Long> accountIds = redisService.getAllWeeklyLoveActionAccountIds();

        if (recipeIds.isEmpty() && accountIds.isEmpty()) {
            // Không có dữ liệu Redis luôn → return null hoặc throw exception tùy ý
            return null;
        }

        // Get Top 3 account & recipe từ Redis
        List<WeeklyAccountDto> topAccounts = redisService.getTop3AccountsByLoveActionCount(accountIds);
        List<WeeklyRecipeDto> topRecipes = redisService.getTop3WeeklyRecipe(recipeIds);

        // Gọi AccountClient để lấy thông tin chi tiết account
        List<AccountResponse> accountResponses = accountClient.getAccountDetail(
                topAccounts.stream().map(WeeklyAccountDto::getAccountId).toList(),
                accessToken);

        // Convert dto → model
        List<WeeklyAccount> weeklyAccounts = weeklyAccountMapper.tWeeklyAccounts(topAccounts, null);

        // Map thành response
        List<WeeklyAccountResponse> accountResponseList = weeklyAccountMapper.toWeeklyAccountResponses(
                accountResponses, weeklyAccounts);

        List<WeeklyRecipeResponse> recipeResponseList = topRecipes.stream()
                .map(dto -> new WeeklyRecipeResponse(
                        0L, // id = 0 vì chưa có trong DB
                        dto.getRecipeId(),
                        dto.getLoveCount(),
                        dto.getRank()))
                .toList();

        // Trả về WeeklyTrendingResponse
        return WeeklyTrendingResponse.builder()
                .id(0L)
                .week(week)
                .year(year)
                .weeklyAccounts(accountResponseList)
                .weeklyRecipes(recipeResponseList)
                .build();
    }

}

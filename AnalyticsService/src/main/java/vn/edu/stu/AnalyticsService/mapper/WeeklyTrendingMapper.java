package vn.edu.stu.AnalyticsService.mapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.AnalyticsService.client.AccountClient;
import vn.edu.stu.AnalyticsService.dto.WeeklyAccountDto;
import vn.edu.stu.AnalyticsService.model.WeeklyAccount;
import vn.edu.stu.AnalyticsService.model.WeeklyTrending;
import vn.edu.stu.AnalyticsService.response.WeeklyTrendingResponse;
import vn.edu.stu.common_dto.dto.AccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyAccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyRecipeResponse;

@Component
@RequiredArgsConstructor
public class WeeklyTrendingMapper {

    private final AccountClient accountClient;
    private final WeeklyRecipeMapper weeklyRecipeMapper;
    private final WeeklyAccountMapper weeklyAccountMapper;


    public WeeklyTrendingResponse toWeeklyTrendingResponse(WeeklyTrending trending, String accessToken) {
        WeeklyTrendingResponse response = new WeeklyTrendingResponse();
        response.setId(trending.getId());
        response.setWeek(trending.getWeek());
        response.setYear(trending.getYear());

        // 1. Map WeeklyAccounts
        List<WeeklyAccount> weeklyAccounts = trending.getWeeklyAccounts();
        List<Long> accountIds = weeklyAccounts.stream()
                .map(WeeklyAccount::getAccountId)
                .collect(Collectors.toList());

        List<AccountResponse> accountResponses = accountClient.getAccountDetail(accountIds, accessToken);
        List<WeeklyAccountResponse> weeklyAccountResponses = weeklyAccountMapper
                .toWeeklyAccountResponses(accountResponses, weeklyAccounts);
        response.setWeeklyAccounts(weeklyAccountResponses);

        // 2. Map WeeklyRecipes (không gọi recipe-service ở đây)
        List<WeeklyRecipeResponse> weeklyRecipeResponses = weeklyRecipeMapper
                .toWeeklyRecipeResponses(trending.getWeeklyRecipes());
        response.setWeeklyRecipes(weeklyRecipeResponses);

        return response;
    }

}

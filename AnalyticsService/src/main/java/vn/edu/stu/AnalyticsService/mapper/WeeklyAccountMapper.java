package vn.edu.stu.AnalyticsService.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.edu.stu.AnalyticsService.dto.WeeklyAccountDto;
import vn.edu.stu.AnalyticsService.model.WeeklyAccount;
import vn.edu.stu.AnalyticsService.model.WeeklyTrending;
import vn.edu.stu.common_dto.dto.AccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyAccountResponse;

@Component
public class WeeklyAccountMapper {

    public WeeklyAccount toWeeklyAccount(WeeklyAccountDto dto, WeeklyTrending weeklyTrending) {
        return WeeklyAccount.builder()
                .accountId(dto.getAccountId())
                .totalLoveAction(dto.getTotalLoveAction())
                .rank(dto.getRank())
                .build();
    }

    public List<WeeklyAccount> tWeeklyAccounts(List<WeeklyAccountDto> dtos, WeeklyTrending weeklyTrending) {
        return dtos.stream()
                .map(dto -> this.toWeeklyAccount(dto, weeklyTrending))
                .sorted(Comparator.comparing(WeeklyAccount::getTotalLoveAction).reversed()) // reversed đảo ngược mảng
                                                                                            // sắp xếp tăng->sx giảm
                .toList();
    }

    public WeeklyAccountResponse toWeeklyAccountResponse(AccountResponse accountResponse, WeeklyAccount weeklyAccount) {
        WeeklyAccountResponse response = new WeeklyAccountResponse();
        response.setId(weeklyAccount.getId());
        response.setAccountId(weeklyAccount.getAccountId());
        response.setAvatarUrl(accountResponse.getAvatarUrl());
        response.setCreatedAt(accountResponse.getCreatedAt());
        response.setEmail(accountResponse.getEmail());
        response.setRank(weeklyAccount.getRank());
        response.setRole(accountResponse.getRole());
        response.setUpdatedAt(accountResponse.getUpdatedAt());
        response.setUsername(accountResponse.getUsername());
        response.setTotalLoveAction(weeklyAccount.getTotalLoveAction());

        return response;
    }

    public List<WeeklyAccountResponse> toWeeklyAccountResponses(List<AccountResponse> accountResponses,
            List<WeeklyAccount> dtos) {
        Map<Long, WeeklyAccount> dtoMap = dtos.stream()
                .collect(Collectors.toMap(WeeklyAccount::getAccountId, Function.identity()));

        List<WeeklyAccountResponse> responses = accountResponses.stream()
                .map(account -> toWeeklyAccountResponse(account, dtoMap.get(account.getId())))
                .collect(Collectors.toList());

        return responses;
    }
}

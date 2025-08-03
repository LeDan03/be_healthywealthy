package vn.edu.stu.AnalyticsService.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.stu.common_dto.dto.WeeklyAccountResponse;
import vn.edu.stu.common_dto.dto.WeeklyRecipeResponse;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeeklyTrendingResponse {
    private long id;
    private int week;
    private int year;

    private List<WeeklyAccountResponse> weeklyAccounts;
    private List<WeeklyRecipeResponse> weeklyRecipes; // chỉ chứa id, rank, count. Gọi api để lấy thông tin recipe
}
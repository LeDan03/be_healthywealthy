package vn.edu.stu.AnalyticsService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeeklyAccountDto {
    private long accountId;
    private int totalLoveAction;
    private int rank;
}

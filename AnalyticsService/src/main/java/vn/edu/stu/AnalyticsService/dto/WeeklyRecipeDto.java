package vn.edu.stu.AnalyticsService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeeklyRecipeDto {
    private long recipeId;
    private int loveCount;
    private int rank;

}

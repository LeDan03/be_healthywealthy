package vn.edu.stu.common_dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WeeklyRecipeResponse {
    private long id;
    private long recipeId;
    private int loveCount;
    private int rank;
}

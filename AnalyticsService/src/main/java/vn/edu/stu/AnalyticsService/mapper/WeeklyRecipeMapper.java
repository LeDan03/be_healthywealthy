package vn.edu.stu.AnalyticsService.mapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.AnalyticsService.dto.WeeklyRecipeDto;
import vn.edu.stu.AnalyticsService.model.WeeklyRecipe;
import vn.edu.stu.AnalyticsService.model.WeeklyTrending;
import vn.edu.stu.common_dto.dto.WeeklyRecipeResponse;

@Component
@RequiredArgsConstructor
public class WeeklyRecipeMapper {

    public WeeklyRecipe toWeeklyRecipe(WeeklyRecipeDto dto, WeeklyTrending weeklyTrending) {
        return WeeklyRecipe.builder()
                .recipeId(dto.getRecipeId())
                .loveCount(dto.getLoveCount())
                .rank(dto.getRank())
                .weeklyTrending(weeklyTrending)
                .build();
    }

    public List<WeeklyRecipe> toWeeklyRecipes(List<WeeklyRecipeDto> dtos, WeeklyTrending weeklyTrending) {
        return dtos.stream().map(dto -> this.toWeeklyRecipe(dto, weeklyTrending)).toList();
    }

    public WeeklyRecipeResponse toWeeklyRecipeResponse(WeeklyRecipe weeklyRecipe) {
        return new WeeklyRecipeResponse(weeklyRecipe.getId(), weeklyRecipe.getRecipeId(), weeklyRecipe.getLoveCount(),
                weeklyRecipe.getRank());
    }

    public List<WeeklyRecipeResponse> toWeeklyRecipeResponses(List<WeeklyRecipe> list) {
        return list.stream()
                .map(this::toWeeklyRecipeResponse)
                .collect(Collectors.toList());
    }

}

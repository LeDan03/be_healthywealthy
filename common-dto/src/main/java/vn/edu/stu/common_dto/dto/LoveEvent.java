package vn.edu.stu.common_dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoveEvent {
    private String loveEventId;
    private long recipeId;
    private long accountId;
    private InteractionType type;
}

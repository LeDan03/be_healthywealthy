package vn.edu.stu.common_dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveEvent {
    private String saveEventId;
    private long recipeId;
    private long accountId;
    private InteractionType type;
}

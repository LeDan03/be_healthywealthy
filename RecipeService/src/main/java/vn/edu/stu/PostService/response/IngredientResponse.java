package vn.edu.stu.PostService.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IngredientResponse {
    private String name;
    private double amount;
    private int unitId;
}

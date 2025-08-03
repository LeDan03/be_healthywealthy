package vn.edu.stu.PostService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IngredientRequest {

    private String name;
    private double amount;
    private int unitId;
}

package vn.edu.stu.WebBlogNauAn.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IngredientResponse {
    private String name;
    private int amount;
    private int unitId;
}

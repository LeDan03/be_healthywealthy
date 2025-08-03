package vn.edu.stu.WebBlogNauAn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecipeDto {
    private long recipeId;
    private long accountId;
    private Timestamp saveAt;
}

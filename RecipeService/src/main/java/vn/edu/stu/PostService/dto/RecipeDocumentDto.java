package vn.edu.stu.PostService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDocumentDto {
    private Long id;
    private String title;
    private String content;

    private DifficultyDocument difficulty;
    private CategoryDocument category;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DifficultyDocument {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryDocument {
        private String name;
    }
}

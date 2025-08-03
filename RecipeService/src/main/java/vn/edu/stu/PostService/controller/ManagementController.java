package vn.edu.stu.PostService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.request.CategoryRequest;
import vn.edu.stu.PostService.request.UnitRequest;
import vn.edu.stu.PostService.response.ApiResponse;
import vn.edu.stu.PostService.response.CategoryResponse;
import vn.edu.stu.PostService.service.CategoryService;
import vn.edu.stu.PostService.service.RecipeService;
import vn.edu.stu.PostService.service.UnitService;

@RestController
@RequestMapping("/api/recipes/admin")
@RequiredArgsConstructor
public class ManagementController {
    private final CategoryService categoryService;
    private final UnitService unitService;
    private final RecipeService recipeService;

    @PostMapping("/category")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequest));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable int id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().body(new ApiResponse("Đã xóa phân loại", 200));
    }

    @PostMapping("/unit")
    public ResponseEntity<ApiResponse> createUnit(@RequestBody UnitRequest request) {
        unitService.createUnit(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new ApiResponse("Đã tạo đơn vị mới", 201));
    }

    @DeleteMapping("/unit/{id}")
    public ResponseEntity<ApiResponse> deleteUnit(@PathVariable int id) {
        unitService.deleteById(id);
        return ResponseEntity.ok().body(new ApiResponse("Đã xóa đơn vị", 200));
    }

    // Phan tich cho admin
    @GetMapping("/analytics/recipes/weekly")
    public ResponseEntity<List<Long>> getWeeklyRecipesOfYear(@RequestParam("year") int year) {
        return ResponseEntity.ok().body(recipeService.getWeeklyRecipesOfYear(year));
    }
}

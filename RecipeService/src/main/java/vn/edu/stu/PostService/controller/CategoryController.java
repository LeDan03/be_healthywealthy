package vn.edu.stu.PostService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.stu.PostService.mapper.CategoryMapper;
import vn.edu.stu.PostService.request.CategoryRequest;
import vn.edu.stu.PostService.response.ApiResponse;
import vn.edu.stu.PostService.response.CategoryResponse;
import vn.edu.stu.PostService.response.RecipeResponse;
import vn.edu.stu.PostService.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/recipes/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    public static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    public CategoryController(CategoryService categoryService
    , CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<CategoryResponse> getCategoryById(@RequestParam int categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

}

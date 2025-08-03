package vn.edu.stu.PostService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.service.*;
import vn.edu.stu.PostService.mapper.*;
import vn.edu.stu.PostService.response.*;
import vn.edu.stu.PostService.request.*;
import vn.edu.stu.PostService.model.*;
import vn.edu.stu.PostService.exception.*;
import vn.edu.stu.PostService.dto.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final ImageService imageService;
    private final Logger logger = LoggerFactory.getLogger(RecipeController.class);
    private final RecipeService recipeService;
    private final RecipeSearchService recipeSearchService;
    private final RecipeMapper recipeMapper;
    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<RecipeResponse> addRecipe(@RequestBody RecipeRequest recipeRequest) throws IOException {
        return recipeService.saveRecipe(recipeRequest);
    }

    // Do cần gửi list id qua body nên không dùng Get mapping
    @PostMapping(value = "/by-account-ids")
    public ResponseEntity<List<RecipeResponse>> getRecipesByAccountIds(@RequestBody List<Long> accountIds) {
        logger.info("Getting recipes by accountIds " + accountIds.get(0));
        return ResponseEntity.ok(recipeService.getRecipeByAccountIds(accountIds));
    }

    @PostMapping(value = "/by-ids")
    public ResponseEntity<?> getRecipesByIds(@RequestBody List<Long> recipeIds) {
        List<Recipe> result = recipeService.findAllByIds(recipeIds);
        if (!result.isEmpty()) {
            return ResponseEntity.ok().body(recipeMapper.toResponseList(result));
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getRecipes(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "50", required = false) int size,
            @RequestParam(required = false) Boolean all) {
        if (Boolean.TRUE.equals(all)) {
            if (size != 50 || page != 0) {
                throw new BadRequestException("Bạn không thể vừa lấy phân trang vừa lấy tất cả công thức");
            }
            return recipeService.getAllRecipe();
        } else
            return ResponseEntity.ok(recipeService.getRecipesPageable(page, size));
    }

    @GetMapping("/me/private-recipes")
    public ResponseEntity<?> getPrivateRecipes(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        List<RecipeResponse> result = recipeService.getPrivaterRecipe(myPrincipal.getAccountId());
        if (result.isEmpty()) {
            return ResponseEntity.ok().body(List.of());
        }
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/me/recipes")
    public ResponseEntity<List<RecipeResponse>> getMyPublicRecipes(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        return ResponseEntity.ok().body(recipeService.getPublicRecipesByAccountId(myPrincipal.getAccountId()));
    }

    @PostMapping("/me/{recipeId}/required-public")
    public ResponseEntity<ApiResponse> requiredPublic(@PathVariable long recipeId,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        Boolean result = recipeService.requiredPublic(myPrincipal.getAccountId(), recipeId);
        if (result) {
            return ResponseEntity.ok().body(new ApiResponse("Đã đăng tải bài viết lên cộng đồng", 200));
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(500))
                .body(new ApiResponse("Có lỗi xảy ra, cập nhật thất bại", 500));
    }

    @GetMapping(value = "/{recipeId}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @GetMapping("/{recipeId}/similar")
    public ResponseEntity<List<RecipeResponse>> getSimilarRecipes(
            @PathVariable long recipeId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws IOException {
        List<RecipeResponse> response = recipeService.getSimilarRecipes(keyword, recipeId, page, size);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{accountId}/recipes")
    public ResponseEntity<List<RecipeResponse>> getSomeonePublicRecipes(@PathVariable long accountId) {
        List<RecipeResponse> result = recipeService.getPublicRecipesByAccountId(accountId);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<ApiResponse> updateRecipe(@RequestBody UpdateRecipeRequest updateRecipeRequest,
            @PathVariable long recipeId) {
        recipeService.updateRecipe(recipeId, updateRecipeRequest);
        return ResponseEntity.ok().body(new ApiResponse("Cập nhật thành công", 200));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse> deleteRecipe(@PathVariable long recipeId) {
        MyPrincipal myPrincipal = (MyPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long accountId = myPrincipal.getAccountId();

        recipeService.deleteRecipe(recipeId, accountId);
        return ResponseEntity.ok(new ApiResponse("Đã xóa recipe", 200));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) throws IOException {

        List<RecipeResponse> recipes = recipeService.searchRecipes(keyword, page, size);
        if (!recipes.isEmpty()) {
            return ResponseEntity.ok(recipes);
        } else
            throw new NotFoundException("Không có recipe nào phù họp với key: " + keyword);
    }

    @GetMapping("/{recipeId}/images")
    public ResponseEntity<List<ImageResponse>> getRecipeImages(@PathVariable long recipeId) {
        return ResponseEntity.ok().body(recipeService.getRecipeImages(recipeId));
    }

    // Trả tấm ảnh đầu tiên của mỗi recipe
    @PostMapping("/main-images")
    public ResponseEntity<List<ImageResponse>> getMainImageRecipes(@RequestBody List<Long> recipeIds) {
        return ResponseEntity.ok().body(imageService.mainImagesForRecipes(recipeIds));
    }

    // Unit
    @GetMapping("/units")
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        return ResponseEntity.ok().body(unitService.getAllUnits());
    }

    // TỪ cấm
    @GetMapping("/illegal-word")
    public ResponseEntity<List<TuCamDto>> getAllIllegalWords() {
        return ResponseEntity.ok().body(recipeService.getAllIllegalWord());
    }
}

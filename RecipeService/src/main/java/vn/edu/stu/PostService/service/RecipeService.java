package vn.edu.stu.PostService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import vn.edu.stu.PostService.dto.TuCamDto;
import vn.edu.stu.PostService.dto.UpdateImageDto;
import vn.edu.stu.PostService.exception.ForbiddentException;
import vn.edu.stu.PostService.exception.NotFoundException;
import vn.edu.stu.PostService.mapper.ImageMapper;
import vn.edu.stu.PostService.mapper.RecipeMapper;
import vn.edu.stu.PostService.mapper.StepMapper;
import vn.edu.stu.PostService.model.*;
import vn.edu.stu.PostService.producer.RecipeProducer;
import vn.edu.stu.PostService.repository.CategoryRepo;
import vn.edu.stu.PostService.repository.ImageRepo;
import vn.edu.stu.PostService.repository.RecipeRepo;
import vn.edu.stu.PostService.repository.StepRepo;
import vn.edu.stu.PostService.request.RecipeRequest;
import vn.edu.stu.PostService.request.StepRequest;
import vn.edu.stu.PostService.request.UpdateRecipeRequest;
import vn.edu.stu.PostService.response.ImageResponse;
import vn.edu.stu.PostService.response.RecipeResponse;
import vn.edu.stu.PostService.response.RecipesPageableResponse;
import vn.edu.stu.common_dto.dto.InteractionType;
import vn.edu.stu.common_dto.dto.LoveEvent;
import vn.edu.stu.common_dto.dto.SaveEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepo recipeRepo;
    private final RecipeMapper recipeMapper;
    private final StepMapper stepMapper;
    private final CategoryRepo categoryRepo;
    private final RecipeIndexerService recipeIndexerService;
    private final RecipeSearchService recipeSearchService;
    private final ImageMapper imageMapper;
    private final RecipeProducer recipeProducer;
    private final CloudinaryService cloudinaryService;
    private final ImageRepo imageRepo;
    private final StepRepo stepRepo;

    private final static Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public ResponseEntity<RecipeResponse> saveRecipe(RecipeRequest recipeRequest) throws IOException {
        Recipe recipe = recipeMapper.requestToRecipe(recipeRequest);
        recipe = recipeRepo.save(recipe);
        RecipeResponse recipeResponse;
        try {
            recipeResponse = recipeMapper.toResponse(recipe);
        } catch (Exception e) {
            logger.error("Error mapping recipe to RecipeResponse: ", e);
            throw new RuntimeException("Failed to map Recipe to RecipeResponse", e);
        }
        recipeIndexerService.indexRecipe(recipe);
        recipeProducer.sendCreateRecipeEvent(recipeRequest.getAccountId(), recipe.getId());
        return ResponseEntity.ok(recipeResponse);
    }

    public Recipe getRecipe(long id) {
        return recipeRepo.findById(id).orElse(null);
    }

    public ResponseEntity<List<RecipeResponse>> getAllRecipe() {
        List<RecipeResponse> recipeResponses = recipeMapper.toResponseList(recipeRepo.findByPublishedTrue());
        return ResponseEntity.ok(recipeResponses);
    }

    public List<RecipeResponse> getPublicRecipesByAccountId(long account_id) {
        List<Recipe> recipes = recipeRepo.findByAccountIdAndPublishedTrue(account_id);
        return recipes.stream()
                .map(recipeMapper::toResponse).toList();
    }

    public RecipesPageableResponse getRecipesPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage = recipeRepo.findByPublishedTrue(pageable);

        List<RecipeResponse> recipeResponses = recipeMapper.toResponseList(recipePage.getContent());
        int totalPages = recipePage.getTotalPages();
        long totalElements = recipePage.getTotalElements();

        return new RecipesPageableResponse(recipeResponses, totalPages, totalElements, page);
    }

    @Transactional
    public void updateRecipe(long recipeId, UpdateRecipeRequest updateRecipeRequest) {
        logger.info("REQUEST DATA: {}", updateRecipeRequest);

        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with ID: " + recipeId));
        Category category = categoryRepo.findById(updateRecipeRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        "Category not found with ID: " + updateRecipeRequest.getCategoryId()));

        // Xử lý ảnh
        List<UpdateImageDto> newImages = updateRecipeRequest.getImages();
        Set<String> newImgUrls = newImages.stream()
                .map(UpdateImageDto::getSecureUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Image> oldImages = imageRepo.findByRecipeId(recipeId);
        for (Image i : oldImages) {
            if (!newImgUrls.contains(i.getUrl())) {
                cloudinaryService.deleteImage(i.getPublicId());
            }
        }

        List<Image> images = imageMapper.toImageList(newImages, recipe);
        logger.info("Đã map new images từ request");

        //  KHÔNG set list mới – update trực tiếp vào list hiện tại để tránh lỗi
        // orphanRemoval
        List<Image> currentImages = recipe.getImagesUrl();
        currentImages.clear();
        currentImages.addAll(images);
        logger.info("Đã cập nhật imagesUrl cho recipe: {}", recipe.getImagesUrl());

        if (updateRecipeRequest.getSteps() == null || updateRecipeRequest.getSteps().isEmpty()) {
            recipe.getSteps().clear();
            logger.info("Đã xóa hết recipe steps");
        } else {
            Set<String> newStepImageUrls = updateRecipeRequest.getSteps().stream()
                    .map(StepRequest::getImageUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            logger.info("Đã tạo Set new Step image urls: {}", newStepImageUrls);

            try {
                logger.info("Chuẩn bị load old steps từ DB...");
                List<Step> oldSteps = stepRepo.findByRecipeId(recipeId);
                logger.info("List old steps size đã load được từ db: {}", oldSteps.size());

                for (Step s : oldSteps) {
                    String imageUrl = s.getImageUrl();
                    String publicId = s.getImagePublicId();

                    if (imageUrl != null && !newStepImageUrls.contains(imageUrl) && publicId != null) {
                        cloudinaryService.deleteImage(publicId);
                    }
                }

                List<Step> steps = stepMapper.toSteps(updateRecipeRequest.getSteps(), recipe);
                // logger.info("Đã map new steps thành công");

                List<Step> currentSteps = recipe.getSteps();
                currentSteps.clear();
                currentSteps.addAll(steps);
                logger.info("Đã cập nhật steps mới cho recipe");
            } catch (Exception e) {
                logger.error("Lỗi khi gọi stepRepo.findByRecipeId", e);
                throw e;
            }
        }

        // Cập nhật các trường còn lại
        recipe.setTitle(updateRecipeRequest.getTitle());
        recipe.setContent(updateRecipeRequest.getContent());
        recipe.setCategory(category);

        recipeRepo.save(recipe);
        logger.info("ĐÃ UPDATE recipe: {}", recipe);
    }

    public void deleteRecipe(long recipeId, long accountId) {
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null) {
            throw new NotFoundException("Recipe not found with ID: " + recipeId);
        }
        if (recipe.getAccountId() != accountId) {
            throw new ForbiddentException("Bạn không thể xóa recipe của người khác!");
        }
        recipeRepo.delete(recipe);
        recipeProducer.sendDeleteRecipeEvent(recipeId);
    }

    public RecipeResponse getRecipeById(long id) {
        Recipe recipe = recipeRepo.findById(id).orElse(null);
        if (recipe == null) {
            throw new NotFoundException("Recipe not found with ID: " + id);
        }
        return recipeMapper.toResponse(recipe);
    }

    public List<RecipeResponse> getRecipeByAccountIds(List<Long> accountIds) {
        List<Recipe> recipes = recipeRepo.findByAccountIdInOrderByCreatedAtDesc(accountIds);
        if (recipes.isEmpty()) {
            return new ArrayList<>();
        }
        return recipeMapper.toResponseList(recipes);
    }

    public List<Recipe> findAllByIds(List<Long> recipeIds) {
        List<Recipe> recipes = recipeRepo.findByIdInOrderByCreatedAtDesc(recipeIds);
        if (recipes.isEmpty()) {
            return new ArrayList<>();
        }
        return recipes;
    }

    public boolean applyLoveEvent(LoveEvent event) {
        if (!recipeRepo.existsById(event.getRecipeId())) {
            throw new NotFoundException("Công thức đã bị xóa hoặc không tồn tại!");
        }
        int row = 0;
        if (event.getType() == InteractionType.LOVE) {
            // recipe.setLoveCount(recipe.getLoveCount() + 1);
            row = recipeRepo.incLove(event.getRecipeId());
        } else if (event.getType() == InteractionType.UNLOVE) {
            // recipe.setLoveCount(Math.max(0, recipe.getLoveCount() - 1));
            row = recipeRepo.decLove(event.getRecipeId());
        }
        return row == 1;
    }

    public boolean applySaveEvent(SaveEvent event) {
        if (!recipeRepo.existsById(event.getRecipeId())) {
            throw new NotFoundException("Công thức đã bị xóa hoặc không tồn tại!");
        }
        int row = 0;
        if (event.getType() == InteractionType.SAVE) {
            // recipe.setLoveCount(recipe.getLoveCount() + 1);
            row = recipeRepo.incSave(event.getRecipeId());
        } else if (event.getType() == InteractionType.UNSAVE) {
            // recipe.setLoveCount(Math.max(0, recipe.getLoveCount() - 1));
            row = recipeRepo.decSave(event.getRecipeId());
        }
        return row == 1;
    }

    public List<ImageResponse> getRecipeImages(long recipeId) {
        Optional<Recipe> recipeOpt = recipeRepo.findById(recipeId);
        if (!recipeOpt.isPresent()) {
            return List.of();
        }
        List<Image> recipeImages = recipeOpt.get().getImagesUrl();
        return imageMapper.toResponses(recipeImages);
    }

    public List<RecipeResponse> getPrivaterRecipe(long accountId) {
        return recipeMapper.toResponseList(recipeRepo.findByAccountIdAndPublishedFalse(accountId));
    }

    public boolean requiredPublic(long accountId, long recipeId) {
        int result = recipeRepo.makeRecipePublicById(recipeId);
        recipeProducer.sendCreateRecipeEvent(accountId, recipeId);
        return result > 0;
    }

    public List<RecipeResponse> getSimilarRecipes(String keyword, long recipeId, int page, int size)
            throws IOException {
        try {
            List<Long> allIds = recipeSearchService.searchRecipes(keyword)
                    .stream()
                    .filter(id -> id != recipeId)
                    .collect(Collectors.toList());
            // Kiểm tra nếu allIds trống
            if (allIds.isEmpty()) {
                logger.info("Không tìm thấy SIMILAR RECIPE nào!");
                return List.of(); // Trả về danh sách rỗng nếu không có kết quả tìm kiếm
            }
            logger.info("SIMILAR recipe thứ nhất: {}", allIds.get(0));
            // Tính toán start và end cho phân trang
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, allIds.size());

            if (fromIndex >= allIds.size()) {
                return List.of(); // Trả về rỗng nếu trang vượt quá dữ liệu
            }
            List<Long> pagedIds = allIds.subList(fromIndex, toIndex);
            // Truy vấn các recipe tương ứng
            List<Recipe> recipes = recipeRepo.findByIdInOrderByCreatedAtDesc(pagedIds);
            return recipeMapper.toResponseList(recipes);
        } catch (IOException e) {
            // Xử lý lỗi khi truy vấn Elasticsearch
            logger.error("Error while searching for similar recipes", e);
            throw new RuntimeException("Error while searching for similar recipes", e); // Hoặc trả về thông báo lỗi phù
                                                                                        // hợp
        } catch (Exception e) {
            // Xử lý các lỗi khác
            logger.error("Unexpected error", e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    public List<RecipeResponse> searchRecipes(String keyword, int page, int size)
            throws IOException {
        List<Long> allIds = recipeSearchService.searchRecipes(keyword);

        // Tính toán start và end cho phân trang
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allIds.size());

        if (fromIndex >= allIds.size()) {
            return List.of(); // Trả về rỗng nếu trang vượt quá dữ liệu
        }

        List<Long> pagedIds = allIds.subList(fromIndex, toIndex);

        // Truy vấn các recipe tương ứng
        List<Recipe> recipes = recipeRepo.findByIdInOrderByCreatedAtDesc(pagedIds);
        return recipeMapper.toResponseList(recipes);
    }

    // Danh sách từ cấm
    public List<TuCamDto> getAllIllegalWord() {
        TuCamDto tc1 = new TuCamDto("tởm");
        TuCamDto tc2 = new TuCamDto("gớm");

        return List.of(tc1, tc2);
    }

    // Phuc vu phan tich cho admin
    public Long countWeeklyRecipes(int week, int year) {
        Long count = recipeRepo.countRecipesByWeek(week, year);
        return count != null ? count : 0;
    }

    public List<Long> getWeeklyRecipesOfYear(int year) {
        List<Long> counts = new ArrayList<>();

        int totalWeeks = getTotalWeeksOfYear(year);
        logger.info("2025 TOTAL WEEK: {}", totalWeeks);

        for (int i = 1; i <= totalWeeks; i++) {
            Long count = countWeeklyRecipes(i, year);
            counts.add(count);
            // logger.info("Week {} has {} recipe", i, count);
        }
        return counts;
    }

    private int getTotalWeeksOfYear(int year) {
        LocalDate lastDayOfYear = LocalDate.of(year, 12, 28); // Đảm bảo lấy ngày cuối tuần 52
        WeekFields weekFields = WeekFields.ISO;
        int totalWeeks = lastDayOfYear.get(weekFields.weekOfWeekBasedYear());
        if (totalWeeks == 1) { // Nếu ngày 28/12 thuộc tuần 1 năm sau, thử lùi lại
            lastDayOfYear = lastDayOfYear.minusDays(7);
            totalWeeks = lastDayOfYear.get(weekFields.weekOfWeekBasedYear());
        }
        logger.info("Calculated total weeks for {}: {}", year, totalWeeks);
        return totalWeeks;
    }

}

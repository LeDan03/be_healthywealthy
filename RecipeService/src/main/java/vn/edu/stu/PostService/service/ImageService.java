package vn.edu.stu.PostService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.mapper.ImageMapper;
import vn.edu.stu.PostService.model.Image;
import vn.edu.stu.PostService.repository.ImageRepo;
import vn.edu.stu.PostService.response.ImageResponse;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepo imageRepo;
    private final ImageMapper imageMapper;
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public void save(Image image) {
        imageRepo.save(image);
    }

    public void saveAll(List<Image> images) {
        imageRepo.saveAll(images);
    }

    public List<Image> findByRecipe_Id(long recipeId) {
        return imageRepo.findByRecipeId(recipeId);
    }

    public List<ImageResponse> mainImagesForRecipes(List<Long> recipeIds) {
        List<Image> dbRes = imageRepo.findFirstImagesByRecipeIds(recipeIds);
        logger.info("DB RESPONSE: {}", dbRes);
        if (dbRes.isEmpty()) {
            return List.of();
        }
        return imageMapper.toResponses(dbRes);
    }
}

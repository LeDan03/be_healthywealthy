package vn.edu.stu.PostService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.edu.stu.PostService.model.Image;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {
    List<Image> findByRecipeId(long recipeId);
    //Sub query tao bang tam chua id anh dau tien cua tung recipe
    @Query(value = """
            SELECT *
            FROM image i
            INNER JOIN (
                SELECT MIN(id) AS min_id
                FROM image
                WHERE recipe_id IN (:recipeIds)
                GROUP BY recipe_id
            ) first_images ON i.id = first_images.min_id
            """, nativeQuery = true)
    List<Image> findFirstImagesByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

}

package vn.edu.stu.PostService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.PostService.model.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface RecipeRepo extends JpaRepository<Recipe, Long> {
        Optional<Recipe> findByTitle(String title);

        List<Recipe> findByAccountIdAndPublishedTrue(long accountId);

        List<Recipe> findByPublishedTrue();

        Page<Recipe> findByPublishedTrue(Pageable pageable);

        List<Recipe> findByAccountIdAndPublishedFalse(long accountId);

        List<Recipe> findByAccountIdInOrderByCreatedAtDesc(List<Long> accountIds);

        List<Recipe> findByIdInOrderByCreatedAtDesc(List<Long> ids);

        @Query(value = """
                        SELECT COUNT(*) FROM recipe r
                        WHERE WEEK(r.created_at, 3) = :week
                        AND YEAR(r.created_at) = :year
                        """, nativeQuery = true)
        Long countRecipesByWeek(@Param("week") int week, @Param("year") int year);
        // 3: mode co id 3 cua ISO: tính tuần
        // bắt đầu từ thứ 2. Tuần đầu tiên
        // trong năm là tuần có ít nhất 4
        // ngày trong năm đó

        @Modifying
        @Query(value = """
                        UPDATE Recipe r
                        SET r.featured = true
                        WHERE r.id IN :recipeIds
                        """)
        void updateFeaturedRecipes(@Param("recipeIds") List<Long> recipeIds);

        @Query(value = "SELECT COUNT(r)>0 FROM Recipe r WHERE r.category.id =:categoryId")
        boolean isUsedCategory(@Param("categoryId") int categoryId);

        @Modifying
        @Query(value = "UPDATE recipe SET published = true WHERE id=:id", nativeQuery = true)
        int makeRecipePublicById(@Param("id") long id);// Tra ve so dong bi anh huong

        @Modifying
        @Query(value = "UPDATE recipe SET love_count = love_count + 1 WHERE id = :id", nativeQuery = true)
        int incLove(@Param("id") long id);

        @Modifying
        @Query(value = "UPDATE recipe SET love_count = GREATEST(love_count - 1, 0) WHERE id = :id", nativeQuery = true)
        int decLove(@Param("id") long id);

        @Modifying
        @Query(value = "UPDATE recipe SET save_count = save_count + 1 WHERE id = :id", nativeQuery = true)
        int incSave(@Param("id") long id);

        @Modifying
        @Query(value = "UPDATE recipe SET save_count = GREATEST(save_count - 1, 0) WHERE id = :id", nativeQuery = true)
        int decSave(@Param("id") long id);

        @Modifying
        @Query("UPDATE Recipe r SET r.published = false WHERE r.id =:recipeId")
        void updatePublishedFalseById(@Param("recipeId") long recipeId);

}

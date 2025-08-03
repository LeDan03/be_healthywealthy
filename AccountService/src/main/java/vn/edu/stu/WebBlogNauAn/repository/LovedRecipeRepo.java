package vn.edu.stu.WebBlogNauAn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.stu.WebBlogNauAn.model.LovedRecipe;

public interface LovedRecipeRepo extends JpaRepository<LovedRecipe, Long> {
    Boolean existsByAccountIdAndRecipeId(Long accountId, Long recipeId);

    void deleteByAccountIdAndRecipeId(Long accountId, Long recipeId);

    void deleteByRecipeId(long recipeId);

    @Query("""
            SELECT COUNT(l) FROM LovedRecipe l 
            WHERE FUNCTION('MONTH', l.lovedAt) = :month
            AND FUNCTION('YEAR', l.lovedAt) = :year
            """)
    Long countLoveByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT lr.recipeId  FROM LovedRecipe lr WHERE lr.account.id =:accountId")
    List<Long> findIdsByAccountId(@Param("accountId") long accountId);

    @Query("SELECT lr.recipeId FROM LovedRecipe lr WHERE lr.account.id =:accountId ORDER BY lr.lovedAt DESC")
    List<Long> findIdsByAccountIdSortDes(@Param("accountId") long accountId);
}
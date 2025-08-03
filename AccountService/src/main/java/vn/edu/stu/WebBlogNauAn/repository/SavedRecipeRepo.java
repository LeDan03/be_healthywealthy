package vn.edu.stu.WebBlogNauAn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.stu.WebBlogNauAn.model.SavedRecipe;

import java.util.List;

@Repository
public interface SavedRecipeRepo extends JpaRepository<SavedRecipe, Long> {
    @Query("SELECT sr.recipeId FROM SavedRecipe sr WHERE sr.account.id =:accountId ORDER BY sr.saveAt DESC")
    List<Long> findByAccountIdSortDes(@Param("accountId") long accountId);

    void deleteByAccountIdAndRecipeId(Long accountId, Long recipeId);

    Boolean existsByAccountIdAndRecipeId(Long accountId, Long recipeId);

    void deleteByRecipeId(long recipeId);
}

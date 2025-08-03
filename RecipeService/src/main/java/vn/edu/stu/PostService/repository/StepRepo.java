package vn.edu.stu.PostService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.PostService.model.Step;

@Repository
@Transactional
public interface StepRepo extends JpaRepository<Step, Long> {

    @Query(value = "SELECT s FROM Step s WHERE s.recipe.id=:recipeId")
    List<Step> findByRecipeId(@Param("recipeId") long recipeId);
}

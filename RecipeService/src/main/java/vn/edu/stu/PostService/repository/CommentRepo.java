package vn.edu.stu.PostService.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.edu.stu.PostService.model.Comment;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.parent WHERE c.recipe.id = :recipeId")
    List<Comment> findByRecipeId(@Param("recipeId") long recipeId);

//     @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.recipe.id = :recipeId")
// List<Comment> findByRecipeId(@Param("recipeId") long recipeId);

    @Modifying
    @Query(value = "DELETE FROM Comment c WHERE c.id=:id")
    int deleteById(@Param("id") long id);
}

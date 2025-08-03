package vn.edu.stu.PostService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.stu.PostService.model.Ingredient;

@Repository
public interface IngredientRepo extends JpaRepository<Ingredient, Long> {

    @Query(value = "SELECT COUNT(i)>0 FROM Ingredient i WHERE i.unit.id =:unitId")
    boolean isUsedUnit(@Param("unitId") int unitId);
}

package vn.edu.stu.PostService.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.PostService.model.Category;

import java.util.Optional;

@Repository
@Transactional
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    Optional<Category> findById(int id);

    boolean existsByName(String name);

    @Modifying
    @Query(value = "DELETE FROM Category c WHERE c.id=:id")
    void deleteById(@Param("id") int id);
}

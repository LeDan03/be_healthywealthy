package vn.edu.stu.PostService.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.stu.PostService.model.Unit;

@Repository
public interface UnitRepo extends JpaRepository<Unit, Integer> {
    boolean existsByName(String name);

    Optional<Unit> findByName(String name);

    @Modifying
    @Query(value = "DELETE FROM Unit u WHERE u.id=:id")
    void deleteById(@Param("id") int id);
}

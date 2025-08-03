package vn.edu.stu.WebBlogNauAn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.stu.WebBlogNauAn.model.Reason;

@Repository
public interface ReasonRepo extends JpaRepository<Reason, Integer> {
    Optional<Reason> findById(int id);

    @Query(value = "SELECT r.id FROM Reason r WHERE r.relatedEntityType=:relatedEntityType")
    Integer findIdByRelatedEntityType(@Param("relatedEntityType") String relatedEntityType);

    @Modifying
    @Query(value = "DELETE FROM Reason r WHERE r.id=:id")
    void deleteById(@Param("id") int id);
}

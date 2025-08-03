package vn.edu.stu.PostService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.stu.PostService.model.Difficulty;

@Repository
public interface DifficultyRepo extends JpaRepository<Difficulty, Integer> {
    boolean existsByName(String name);
    Optional<Difficulty> findByName(String name);
}

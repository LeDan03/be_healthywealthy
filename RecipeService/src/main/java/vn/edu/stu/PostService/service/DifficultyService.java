package vn.edu.stu.PostService.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.stu.PostService.exception.ConflictException;
import vn.edu.stu.PostService.model.Difficulty;
import vn.edu.stu.PostService.repository.DifficultyRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DifficultyService {
    private final DifficultyRepo difficultyRepo;

    @PostConstruct
    public void initDifficulty() {
        List<String> levels = List.of("Dễ", "Trung bình", "Khó");
        for (String level : levels) {
            if (!difficultyRepo.existsByName(level)) {
                Difficulty difficulty = new Difficulty();
                difficulty.setName(level);
                difficultyRepo.save(difficulty);
            }
        }
    }

    public Difficulty createDifficulty(String name) {
        if (difficultyRepo.existsByName(name)) {
            throw new ConflictException("Cấp độ " + "'" + name + "'" + "đã tồn tại!");
        }
        Difficulty difficulty = new Difficulty();
        difficulty.setName(name);
        difficultyRepo.save(difficulty);
        return difficulty;
    }

    public List<Difficulty> getDifficulties() {
        return difficultyRepo.findAll();
    }
}

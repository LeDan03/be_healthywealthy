package vn.edu.stu.PostService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.edu.stu.PostService.exception.NotFoundException;
import vn.edu.stu.PostService.mapper.DifficultyMapper;
import vn.edu.stu.PostService.request.DifficultyRequest;
import vn.edu.stu.PostService.response.DifficultyResponse;
import vn.edu.stu.PostService.service.DifficultyService;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/difficulties")
public class DifficultyController {
    private  final DifficultyService difficultyService;
    private final DifficultyMapper difficultyMapper;

    @Autowired
    public DifficultyController(DifficultyService difficultyService
            , DifficultyMapper difficultyMapper) {
        this.difficultyService = difficultyService;
        this.difficultyMapper = difficultyMapper;
    }

    @PostMapping
    public ResponseEntity<DifficultyResponse> createDifficulty(@RequestBody DifficultyRequest difficultyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(difficultyMapper.toResponse(difficultyService.createDifficulty(difficultyRequest.getName())));
    }
    @GetMapping
    public ResponseEntity<List<DifficultyResponse>> getDifficulties() {
        if(difficultyService.getDifficulties().isEmpty()){
            throw new NotFoundException("There are no difficulties");
        }
        return ResponseEntity.ok(difficultyMapper.toResponseList(difficultyService.getDifficulties()));
    }
}

package vn.edu.stu.PostService.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.PostService.model.Difficulty;
import vn.edu.stu.PostService.response.DifficultyResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DifficultyMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public DifficultyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DifficultyResponse toResponse(Difficulty difficulty) {
        return DifficultyResponse.builder()
        .id(difficulty.getId())
        .name(difficulty.getName())
        .build();
    }
    public List<DifficultyResponse> toResponseList(List<Difficulty> difficultyList) {
        return difficultyList.stream().map(this::toResponse).collect(Collectors.toList());
    }
}

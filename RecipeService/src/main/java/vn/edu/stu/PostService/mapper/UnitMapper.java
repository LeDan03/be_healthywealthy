package vn.edu.stu.PostService.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.edu.stu.PostService.model.Unit;
import vn.edu.stu.PostService.response.UnitResponse;

@Component
public class UnitMapper {
    public UnitResponse toResponse(Unit unit) {
        return UnitResponse.builder()
        .id(unit.getId())
        .name(unit.getName())
        .build();
    }

    public List<UnitResponse> toResponseList(List<Unit> units) {
        return units.stream().map(this::toResponse).toList();
    }
}

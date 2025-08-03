package vn.edu.stu.PostService.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.model.Step;
import vn.edu.stu.PostService.request.StepRequest;
import vn.edu.stu.PostService.response.StepResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StepMapper {
    private final ModelMapper modelMapper;

    public Step toStep(StepRequest stepRequest, Recipe recipe) {
        return Step.builder()
                .content(stepRequest.getContent())
                .imageUrl(stepRequest.getImageUrl() != null ? stepRequest.getImageUrl() : "")
                .stt(stepRequest.getStt())
                .imagePublicId(stepRequest.getImagePublicId() != null ? stepRequest.getImagePublicId() : "")
                .recipe(recipe)
                .build();
    }

    public List<Step> toSteps(List<StepRequest> stepStrList, Recipe recipe) {
        return stepStrList.stream().map(step -> toStep(step, recipe)).toList();
    }

    public String toStepStr(Step step) {
        return step.getContent();
    }

    public StepResponse toResponse(Step step) {
        return modelMapper.map(step, StepResponse.class);
    }

    public List<StepResponse> toResponses(List<Step> steps) {
        return steps.stream().map(this::toResponse).toList();
    }
}

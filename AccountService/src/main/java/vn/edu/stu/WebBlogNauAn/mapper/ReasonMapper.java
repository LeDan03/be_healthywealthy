package vn.edu.stu.WebBlogNauAn.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.model.Reason;
import vn.edu.stu.WebBlogNauAn.request.ReasonRequest;
import vn.edu.stu.WebBlogNauAn.response.ReasonResponse;

@Component
@RequiredArgsConstructor
public class ReasonMapper {

    public Reason tReason(ReasonRequest reasonRequest) {
        return Reason.builder()
                .message(reasonRequest.getContent())
                .relatedEntityType(reasonRequest.getRelatedEntityType())
                .build();
    }

    public ReasonResponse tResponse(Reason reason) {
        return ReasonResponse.builder()
                .id(reason.getId())
                .message(reason.getMessage())
                .relatedEntityType(reason.getRelatedEntityType())
                .build();
    }

    public List<ReasonResponse> tResponses(List<Reason> reasons) {
        return reasons.stream().map(this::tResponse).toList();
    }
}

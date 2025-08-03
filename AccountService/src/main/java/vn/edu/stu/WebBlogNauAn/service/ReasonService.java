package vn.edu.stu.WebBlogNauAn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.exception.BadRequestException;
import vn.edu.stu.WebBlogNauAn.mapper.ReasonMapper;
import vn.edu.stu.WebBlogNauAn.model.Reason;
import vn.edu.stu.WebBlogNauAn.repository.MessageRepo;
import vn.edu.stu.WebBlogNauAn.repository.ReasonRepo;
import vn.edu.stu.WebBlogNauAn.request.ReasonRequest;
import vn.edu.stu.WebBlogNauAn.response.ReasonResponse;

@Service
@RequiredArgsConstructor
public class ReasonService {

    private final ReasonRepo reasonRepo;
    private final ReasonMapper reasonMapper;
    private final MessageRepo messageRepo;

    @PostConstruct
    private void initDefaultReason() {
        if (reasonRepo.existsById(1)) {
            return;
        }
        Reason dfReason = new Reason();
        dfReason.setMessage(
                "NỘI DUNG KHÔNG PHÙ HỢP");
        dfReason.setRelatedEntityType("RECIPE");

        Reason adminTask = new Reason();
        adminTask.setMessage("QUẢN LÝ");
        adminTask.setRelatedEntityType("ADMIN");

        Reason community = new Reason();
        community.setMessage("THÔNG BÁO CHUNG");
        community.setRelatedEntityType("COMMUNITY");

        reasonRepo.saveAll(List.of(dfReason, adminTask, community));
    }

    public List<ReasonResponse> getReasons() {
        return reasonMapper.tResponses(reasonRepo.findAll());
    }

    public ReasonResponse createReason(ReasonRequest reasonRequest) {
        Reason reason = reasonMapper.tReason(reasonRequest);
        reasonRepo.save(reason);
        return reasonMapper.tResponse(reason);
    }

    public void deleteReason(int reasonId) {
        if (messageRepo.isUseReasonById(reasonId)) {
            throw new BadRequestException("Lý do đã được áp dụng, không thể xóa");
        }
        reasonRepo.deleteById(reasonId);
    }
}

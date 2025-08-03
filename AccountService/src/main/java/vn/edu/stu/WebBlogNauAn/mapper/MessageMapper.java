package vn.edu.stu.WebBlogNauAn.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.model.CommunityMessage;
import vn.edu.stu.WebBlogNauAn.model.Message;
import vn.edu.stu.WebBlogNauAn.model.Reason;
import vn.edu.stu.WebBlogNauAn.repository.ReasonRepo;
import vn.edu.stu.WebBlogNauAn.request.MessageRequest;
import vn.edu.stu.WebBlogNauAn.response.MessageResponse;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final ReasonRepo reasonRepo;
    private static final Logger log = LoggerFactory.getLogger(MessageRequest.class);

    public Message tMessage(MessageRequest request) {
        Optional<Reason> reOptional = reasonRepo.findById(request.getReasonId());
        if (!reOptional.isPresent()) {
            throw new ResourceNotFoundException("Nguyên nhân (reason) được chọn không tồn tại");
        }
        Reason reason = reOptional.get();
        return Message.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .reason(reason)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    public CommunityMessage tCommunityMessage(MessageRequest request) {
        Optional<Reason> reOptional = reasonRepo.findById(request.getReasonId());
        if (!reOptional.isPresent()) {
            throw new ResourceNotFoundException("Nguyên nhân (reason) được chọn không tồn tại");
        }
        Reason reason = reOptional.get();

        return CommunityMessage.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .reason(reason)
                .pinned(false)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    public MessageResponse tResponse(Message message) {
        try {
            MessageResponse.MessageResponseBuilder builder = MessageResponse.builder()
                    .id(message.getId())
                    .title(message.getTitle())
                    .reasonId(message.getReason().getId())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .sentToAll(message.isSentToAll());

            // Nếu là CommunityMessage thì set thêm pinned
            if (message instanceof CommunityMessage) {
                CommunityMessage cm = (CommunityMessage) message;
                builder.pinned(cm.getPinned());
            }

            return builder.build();
        } catch (Exception e) {
            log.info("MAP message sang response thất bại");
            return new MessageResponse();
        }
    }

    public List<MessageResponse> tResponses(List<Message> messages) {
        return messages.stream().map(this::tResponse).toList();
    }
}

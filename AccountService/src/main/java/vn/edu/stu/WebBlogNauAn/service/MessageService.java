package vn.edu.stu.WebBlogNauAn.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.exception.BadRequestException;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.mapper.AccountMessageMapper;
import vn.edu.stu.WebBlogNauAn.mapper.MessageMapper;
import vn.edu.stu.WebBlogNauAn.model.AccountMessage;
import vn.edu.stu.WebBlogNauAn.model.CommunityMessage;
import vn.edu.stu.WebBlogNauAn.model.Message;
import vn.edu.stu.WebBlogNauAn.model.Reason;
import vn.edu.stu.WebBlogNauAn.repository.AccountMessageRepo;
import vn.edu.stu.WebBlogNauAn.repository.MessageRepo;
import vn.edu.stu.WebBlogNauAn.repository.ReasonRepo;
import vn.edu.stu.WebBlogNauAn.request.AccountMessageRequest;
import vn.edu.stu.WebBlogNauAn.request.MessageRequest;
import vn.edu.stu.WebBlogNauAn.response.AccountMessageResponse;
import vn.edu.stu.WebBlogNauAn.response.MessageResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepo messageRepo;
    private final MessageMapper messageMapper;
    private final ReasonRepo reasonRepo;
    private final AccountMessageRepo accountMessageRepo;
    private final AccountMessageMapper accountMessageMapper;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    public MessageResponse createNotification(MessageRequest request) {
        Optional<Reason> rOpt = reasonRepo.findById(request.getReasonId());
        if (!rOpt.isPresent()) {
            throw new ResourceNotFoundException("Nguyên nhân không tồn tại");
        }
        Message message;
        Reason r = rOpt.get();
        if (r.getRelatedEntityType().equals("COMMUNITY")) {
            CommunityMessage cmm = messageMapper.tCommunityMessage(request);
            cmm.setPinned(false);
            message = cmm;
        } else {
            message = messageMapper.tMessage(request);
        }
        messageRepo.save(message);

        return messageMapper.tResponse(message);
    }

    public void sendNotificationToVUser(AccountMessageRequest request) {
        AccountMessage accountMessage = accountMessageMapper.tAccountMessage(request);
        accountMessageRepo.save(accountMessage);
    }

    public List<AccountMessageResponse> getAccountMessages(long accountId) {
        List<AccountMessage> messages = accountMessageRepo.findByAccountIdOrderByCreatedAtDesc(accountId);
        if (messages.isEmpty()) {
            return List.of();
        }

        return accountMessageMapper.tAccountMessageResponses(messages);
    }

    public void readMessage(long id) {
        accountMessageRepo.updateReadTrueById(id);
    }

    public void sendToAll(long messageId) {
        messageRepo.insertMessageToAllAccount(messageId);
        messageRepo.updateSentToAllTrue(messageId);
    }

    public void togglePinned(long messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message không tồn tại"));

        if (!(message instanceof CommunityMessage)) {
            throw new BadRequestException("Chỉ CommunityMessage mới có thể được pinned");
        }

        CommunityMessage cm = (CommunityMessage) message;
        // tự động hủy cái pinned cũ
        if (cm.getPinned() == null || !cm.getPinned()) {
            messageRepo.findFirstByPinnedTrue().ifPresent(old -> {
                old.setPinned(false);
                messageRepo.save(old);
            });
            cm.setPinned(true);
        } else {
            cm.setPinned(false);
        }
        messageRepo.save(cm);

    }

    public Page<MessageResponse> getAdminMessages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messagesPage = messageRepo.findAll(pageable);

        return new PageImpl<>(
                messageMapper.tResponses(messagesPage.getContent()),
                pageable,
                messagesPage.getTotalElements());
    }

    public void deleteAdminMessageById(long id) {
        Optional<Message> mOpt = messageRepo.findById(id);
        if (!mOpt.isPresent()) {
            throw new ResourceNotFoundException("Thông báo cần xóa không tồn tại");
        }
        if (accountMessageRepo.existsByMessageId(id)) {
            throw new BadRequestException("Thông báo đã gửi tới người dùng, không thể xóa");
        }
        messageRepo.deleteById(id);
    }

    public MessageResponse getPinnedMessage() {
        Optional<CommunityMessage> pinnedMessageOpt = messageRepo.findFirstByPinnedTrue();

        if (!pinnedMessageOpt.isPresent()) {
            return null;
        }

        Message pinnedMessage = pinnedMessageOpt.get();

        return messageMapper.tResponse(pinnedMessage);
    }
}

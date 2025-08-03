package vn.edu.stu.WebBlogNauAn.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.model.AccountMessage;
import vn.edu.stu.WebBlogNauAn.model.Message;
import vn.edu.stu.WebBlogNauAn.repository.AccountRepo;
import vn.edu.stu.WebBlogNauAn.repository.MessageRepo;
import vn.edu.stu.WebBlogNauAn.request.AccountMessageRequest;
import vn.edu.stu.WebBlogNauAn.response.AccountMessageResponse;

@Component
@RequiredArgsConstructor
public class AccountMessageMapper {

    private final AccountRepo accountRepo;
    private final MessageRepo messageRepo;

    public AccountMessage tAccountMessage(AccountMessageRequest request) {

        Optional<Account> accOptional = accountRepo.findById(request.getAccountId());
        if (!accOptional.isPresent()) {
            throw new ResourceNotFoundException("Không có người dùng này!");
        }

        Optional<Message> mesOptional = messageRepo.findById(request.getMessageId());
        if (!mesOptional.isPresent()) {
            throw new ResourceNotFoundException("Không có thông báo này!");
        }

        Account account = accOptional.get();
        Message message = mesOptional.get();

        return AccountMessage.builder()
                .account(account)
                .message(message)
                .createdAt(Timestamp.from(Instant.now()))
                .isRead(false)
                .build();
    }

    public AccountMessageResponse tAccountMessageResponse(AccountMessage accountMessage) {
        return AccountMessageResponse.builder()
                .id(accountMessage.getId())
                .title(accountMessage.getMessage().getTitle())
                .content(accountMessage.getMessage().getContent())
                .createdAt(accountMessage.getCreatedAt())
                .read(accountMessage.isRead())
                .accountId(accountMessage.getAccount().getId())
                .messageId(accountMessage.getMessage().getId())
                .build();
    }

    public List<AccountMessageResponse> tAccountMessageResponses(List<AccountMessage> accountMessages) {
        return accountMessages.stream().map(this::tAccountMessageResponse).collect(Collectors.toList());
    }
}

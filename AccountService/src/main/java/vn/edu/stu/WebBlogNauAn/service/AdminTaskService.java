package vn.edu.stu.WebBlogNauAn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.exception.BadRequestException;
import vn.edu.stu.WebBlogNauAn.exception.IllegalException;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.mapper.AccountMapper;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.producer.NotificationProducer;
import vn.edu.stu.WebBlogNauAn.repository.AccountRepo;
import vn.edu.stu.WebBlogNauAn.repository.LovedRecipeRepo;
import vn.edu.stu.WebBlogNauAn.repository.MessageRepo;
import vn.edu.stu.WebBlogNauAn.request.AccountMessageRequest;
import vn.edu.stu.WebBlogNauAn.response.AccountPageableResponse;
import vn.edu.stu.WebBlogNauAn.response.AccountResponse;
import vn.edu.stu.WebBlogNauAn.response.RecipeResponse;
import vn.edu.stu.common_dto.dto.CreateRecipeEvent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminTaskService {

    private final RedisService redisService;
    private final RecipeClient recipeClient;
    private final MessageService messageService;
    private final MessageRepo messageRepo;
    private final NotificationProducer notificationProducer;
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    private final LovedRecipeRepo lovedRecipeRepo;

    public List<RecipeResponse> getAllUnResolvedRecipes(String accessToken) {
        List<CreateRecipeEvent> unResolvedEvents = redisService.getPendingApprovalEvents();
        List<Long> recipeIds = unResolvedEvents.stream().map(CreateRecipeEvent::getRecipeId).toList();

        List<RecipeResponse> unResolvedRecipes = recipeClient.getRecipesByIds(recipeIds, accessToken);
        return unResolvedRecipes;
    }

    public void approveAnRecipe(long recipeId) {
        redisService.removeReviewedRecipe(recipeId);
    }

    public void rejectAnRecipe(AccountMessageRequest accountMessageRequest) {
        // Tao 1 message thong bao toi nguoi dung
        messageService.sendNotificationToVUser(accountMessageRequest);
        // Gui 1 su kien rejectRecipe toi recipe-service de set published= false
        notificationProducer.sendRejectRecipeEvent(accountMessageRequest.getRecipeId());
        // Sau khi gui su kien toi recipe-service nghia la da review nen xoa ra khoi
        // hang doi cua redis
        redisService.removeReviewedRecipe(accountMessageRequest.getRecipeId());
    }

    public List<RecipeResponse> getPendingRecipes(String accessToken) {
        List<CreateRecipeEvent> events = redisService.getPendingApprovalEvents();
        List<Long> ids = events.stream().map(CreateRecipeEvent::getRecipeId).toList();

        List<RecipeResponse> recipes = recipeClient.getRecipesByIds(ids, accessToken);
        return recipes;
    }

    public AccountPageableResponse getAccountPageableResponse(int page, int size) {
        if (page < 0 || size < 0) {
            throw new BadRequestException("Trang yêu cầu không tồn tại");
        }
        if (size < 10) {
            throw new IllegalException("Số lượng tài khoản tối thiểu là 10");
        }
        if (size > 50) {
            throw new IllegalException("Số lượng tài khoản mỗi trang phải bé hơn 50");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountPage = accountRepo.findAll(pageable);
        int totalPages = accountPage.getTotalPages();

        if (page > totalPages) {
            throw new BadRequestException("Trang yêu cầu vượt giới hạn");
        }

        List<AccountResponse> accountResponses = accountMapper.toAccountResponseList(accountPage.getContent());
        long totalAccounts = accountPage.getTotalElements();
        boolean isLast = accountPage.isLast();

        return new AccountPageableResponse(accountResponses, totalPages, page, size, totalAccounts, isLast);
    }

    public void sendNotifyToAll(long messageId) {
        if (!messageRepo.existsById(messageId))
            throw new ResourceNotFoundException("Thông báo không tồn tại!");
        messageRepo.insertMessageToAllAccount(messageId);
        messageRepo.updateSentToAllTrue(messageId);
    }

    // Thống kê
    // register
    public long countMonthlyRegisteredAccounts(int month, int year) {
        Long count = accountRepo.countAccountsRegisteredByMonth(month, year);
        return count != null ? count : 0;
    }

    public List<Long> getMonthlyRegisteredAccountsOfYear(int year) {
        List<Long> counts = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Long count = countMonthlyRegisteredAccounts(i, year);
            counts.add(count);
        }

        return counts;
    }

    // love action
    public long countMonthlyRecipeLoves(int month, int year) {
        Long count = lovedRecipeRepo.countLoveByMonth(month, year);
        return count != null ? count : 0;
    }

    public List<Long> getMonthlyRecipeLovesOfYear(int year) {
        List<Long> counts = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Long count = countMonthlyRecipeLoves(i, year);
            counts.add(count);
        }

        return counts;
    }
}

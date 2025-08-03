package vn.edu.stu.WebBlogNauAn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.dto.CommenterDto;
import vn.edu.stu.WebBlogNauAn.dto.MyPrincipal;
import vn.edu.stu.WebBlogNauAn.dto.UpdateAvatarDto;
import vn.edu.stu.WebBlogNauAn.dto.UpdateUsernameDto;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.repository.AccountRepo;
import vn.edu.stu.WebBlogNauAn.response.AccountMessageResponse;
import vn.edu.stu.WebBlogNauAn.response.AccountResponse;
import vn.edu.stu.WebBlogNauAn.response.ApiResponse;
import vn.edu.stu.WebBlogNauAn.response.MessageResponse;
import vn.edu.stu.WebBlogNauAn.response.RecipeResponse;
import vn.edu.stu.WebBlogNauAn.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.edu.stu.WebBlogNauAn.service.InteractionService;
import vn.edu.stu.WebBlogNauAn.service.MessageService;
import vn.edu.stu.WebBlogNauAn.service.RecipeClient;

import java.util.List;

@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
public class MainController {

    private final AccountService accountService;
    private final InteractionService interactionService;
    private final RecipeClient recipeClient;
    private final MessageService messageService;
    private final AccountRepo accountRepo;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable long accountId) {
        AccountResponse accountResponse = accountService.findAccountById(accountId);
        if (accountResponse == null) {
            throw new ResourceNotFoundException("Account with id " + accountId + " not found");
        }
        return ResponseEntity.ok(accountResponse);
    }

    @PostMapping
    public ResponseEntity<List<AccountResponse>> getAccountsByIds(@RequestBody List<Long> accountIds) {
        return ResponseEntity.ok().body(accountService.findByIds(accountIds));
    }

    @GetMapping(value = "/me")
    public ResponseEntity<?> getMyData(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        String email = myPrincipal.getEmail();
        AccountResponse account = accountService.findAccountByEmail(email);
        if (account == null) {
            throw new ResourceNotFoundException("Account with email " + email + " not found");
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping("/me")
    public ResponseEntity<ApiResponse> toggleFollow(@RequestParam long followeeId,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long followerId = myPrincipal.getAccountId();
        interactionService.toggleFollow(followeeId, followerId);
        return ResponseEntity.ok(new ApiResponse("Đã theo dõi!", 200));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<ApiResponse> updateAvatarUrl(@RequestBody UpdateAvatarDto updateAccountDto,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        String email = myPrincipal.getEmail();
        accountService.updateAvatar(updateAccountDto, email);
        return ResponseEntity.ok(new ApiResponse("Đã cập nhật ảnh đại diện", 200));
    }

    @PutMapping("/me/username")
    public ResponseEntity<ApiResponse> updateUsername(@RequestBody UpdateUsernameDto dto,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        String email = myPrincipal.getEmail();
        accountService.updateUsername(dto, email);
        return ResponseEntity.ok(new ApiResponse("Đã cập nhật tên người dùng", 200));
    }

    @GetMapping("/me/saved-recipes")
    public ResponseEntity<List<RecipeResponse>> mySavedRecipes(@CookieValue("accessToken") String accessToken,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long accountId = myPrincipal.getAccountId();
        return ResponseEntity.ok(interactionService.getAccountSavedRecipe(accountId, accessToken));
    }

    @GetMapping("/me/loved-recipes")
    public ResponseEntity<List<RecipeResponse>> myLovedRecipes(@CookieValue("accessToken") String accessToken,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long myId = myPrincipal.getAccountId();
        return ResponseEntity.ok().body(interactionService.getAccountLovedRecipes(myId, accessToken));
    }

    @PostMapping("/me/disable-account")
    public ResponseEntity<ApiResponse> deleteAccount() {
        MyPrincipal myPrincipal = (MyPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long accountId = myPrincipal.getAccountId();
        interactionService.disableAccount(accountId);
        return ResponseEntity.ok(new ApiResponse("Tài khoản đã bị vô hiệu", 200));
    }

    @GetMapping(value = "/me/followers")
    public ResponseEntity<List<AccountResponse>> getAllFollowers(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        long myId = myPrincipal.getAccountId();
        return ResponseEntity.ok(interactionService.getAllFollowers(myId));
    }

    @GetMapping(value = "/me/followees")
    public ResponseEntity<List<AccountResponse>> getAllFollowees(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        long myId = myPrincipal.getAccountId();
        return ResponseEntity.ok(interactionService.getAllFollowees(myId));
    }

    @GetMapping("/me/messages")
    public ResponseEntity<List<AccountMessageResponse>> getMyMessage(@AuthenticationPrincipal MyPrincipal myPrincipal) {
        long myId = myPrincipal.getAccountId();
        return ResponseEntity.ok().body(messageService.getAccountMessages(myId));
    }

    @PutMapping("/me/message")
    public ResponseEntity<ApiResponse> readMessgae(@RequestParam long messageId) {
        messageService.readMessage(messageId);
        return ResponseEntity.ok().body(new ApiResponse("Đã cập nhật trạng thái đọc message", 200));
    }

    @GetMapping("/followees/recipes")
    public ResponseEntity<List<RecipeResponse>> getAllFolloweeRecipes(
            @RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long myId = myPrincipal.getAccountId();
        List<Long> followeeIds = interactionService.getAllFolloweeIds(myId);
        if (followeeIds.isEmpty())
            return ResponseEntity.ok(List.of());
        List<RecipeResponse> recipes = recipeClient.getRecipesByAccountIds(followeeIds, accessToken);
        return ResponseEntity.ok(recipes);
    }

    // Interaction
    @PostMapping(value = "/{recipeId}/save")
    public ResponseEntity<?> saveOtherPersonRecipe(@PathVariable long recipeId,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long accountId = myPrincipal.getAccountId();
        interactionService.toggleSaveRecipe(recipeId, accountId);
        return ResponseEntity.ok().body("Đã save");
    }

    @PostMapping(value = "/{recipeId}/love")
    public ResponseEntity<?> loveOtherPersonRecipe(@PathVariable long recipeId,
            @AuthenticationPrincipal MyPrincipal myPrincipal) {
        long accountId = myPrincipal.getAccountId();
        interactionService.toggleLoveRecipe(accountId, recipeId);
        return ResponseEntity.ok().body("Đã yêu thích");
    }

    // Somebody
    @GetMapping("/{accountId}/followees")
    public ResponseEntity<List<AccountResponse>> getAccountFollowees(@PathVariable long accountId) {
        return ResponseEntity.ok().body(interactionService.getAllFollowees(accountId));
    }

    @GetMapping("/{accountId}/followers")
    public ResponseEntity<List<AccountResponse>> getAccountFollowers(@PathVariable long accountId) {
        return ResponseEntity.ok().body(interactionService.getAllFollowers(accountId));
    }

    // Commenter
    @PostMapping("/commenters")
    public ResponseEntity<List<CommenterDto>> getCommentersDetail(@RequestBody List<Long> accountIds) {
        try {
            List<CommenterDto> list = accountRepo.getCommenterDetail(accountIds);
            logger.info("DA LAY DUOC COMMENTER DTOS, cmter thu 1: {}, size: {}", list.get(0).getUsername(),
                    list.size());
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            // TODO: handle exception
            logger.info("LAY thong tin COMMENTERS that bai: {}", e.getMessage());
            return ResponseEntity.ok().body(List.of());
        }

    }

    // search
    @GetMapping(value = "/search", params = "email")
    public ResponseEntity<AccountResponse> searchByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(accountService.findByEmail(email));
    }

    @GetMapping(value = "/search", params = "username")
    public ResponseEntity<List<AccountResponse>> findByUsername(@RequestParam String username) {
        return ResponseEntity.ok().body(accountService.findByUsername(username));
    }
}

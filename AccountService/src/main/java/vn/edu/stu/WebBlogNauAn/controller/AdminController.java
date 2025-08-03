package vn.edu.stu.WebBlogNauAn.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.dto.AdminRegisterDto;
import vn.edu.stu.WebBlogNauAn.dto.RoleRequest;
import vn.edu.stu.WebBlogNauAn.request.AccountMessageRequest;
import vn.edu.stu.WebBlogNauAn.request.AccountsPageableRequest;
import vn.edu.stu.WebBlogNauAn.request.MessageRequest;
import vn.edu.stu.WebBlogNauAn.request.ReasonRequest;
import vn.edu.stu.WebBlogNauAn.response.AccountPageableResponse;
import vn.edu.stu.WebBlogNauAn.response.AccountResponse;
import vn.edu.stu.WebBlogNauAn.response.ApiResponse;
import vn.edu.stu.WebBlogNauAn.response.MessageResponse;
import vn.edu.stu.WebBlogNauAn.response.ReasonResponse;
import vn.edu.stu.WebBlogNauAn.response.RecipeResponse;
import vn.edu.stu.WebBlogNauAn.response.RoleResponse;
import vn.edu.stu.WebBlogNauAn.service.AccountService;
import vn.edu.stu.WebBlogNauAn.service.AdminTaskService;
import vn.edu.stu.WebBlogNauAn.service.MessageService;
import vn.edu.stu.WebBlogNauAn.service.ReasonService;
import vn.edu.stu.WebBlogNauAn.service.RoleService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts/admin")
public class AdminController {

    private final AccountService accountService;
    private final RoleService roleService;
    private final MessageService messageService;
    private final AdminTaskService adminTaskService;
    private final ReasonService reasonService;

    @PostMapping(value = "/role")
    public ResponseEntity<RoleResponse> createNewRole(@RequestBody RoleRequest roleRequest) {
        return roleService.createNewRole(roleRequest);
    }

    @PostMapping(value = "/account")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AdminRegisterDto adminRegisterDto) {
        return accountService.adminCreateAccount(adminRegisterDto);
    }

    // Approve recipe tasks API
    @GetMapping("/pending-recipes")
    public ResponseEntity<List<RecipeResponse>> getPendingRecipes(@CookieValue("accessToken") String accessToken) {
        return ResponseEntity.ok().body(adminTaskService.getPendingRecipes(accessToken));
    }

    @GetMapping("/reasons")
    public ResponseEntity<List<ReasonResponse>> getReasons() {
        return ResponseEntity.ok().body(reasonService.getReasons());
    }

    @PostMapping("/reason")
    public ResponseEntity<ReasonResponse> createReason(@RequestBody ReasonRequest request) {
        ReasonResponse r = reasonService.createReason(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(r);
    }

    @DeleteMapping("/reason/{id}")
    public ResponseEntity<ApiResponse> deleteReasonById(@PathVariable int id) {
        reasonService.deleteReason(id);
        return ResponseEntity.ok().body(new ApiResponse("Đã xóa thành công", 200));
    }

    @GetMapping("/messages")
    public ResponseEntity<Page<MessageResponse>> getAdminMessages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(messageService.getAdminMessages(page, size));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse> deleteAdminMessageById(@PathVariable long id) {
        return ResponseEntity.ok().body(new ApiResponse("Đã xóa thông báo admin", 200));
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> createNotification(@RequestBody MessageRequest messageRequest) {
        MessageResponse rp = messageService.createNotification(messageRequest);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(rp);
    }

    @PostMapping("/messages/account/disability")
    public ResponseEntity<ApiResponse> sendNotificationToVUser(
            @RequestParam long accountId,
            @RequestParam long messageId) {
        messageService.sendNotificationToVUser(new AccountMessageRequest(accountId, messageId, 0));
        return ResponseEntity.ok().body(new ApiResponse("Đã gửi thông báo tới người dùng", 200));
    }

    @PostMapping("/messages/{messages}/send")
    public ResponseEntity<ApiResponse> sendNotificationToAll(@PathVariable long messages) {
        adminTaskService.sendNotifyToAll(messages);
        return ResponseEntity.ok().body(new ApiResponse("Đã gửi thông báo tới toàn bộ người dùng", 200));
    }

    @PostMapping("/messages/{id}/pinned")
    public ResponseEntity<ApiResponse> togglePinned(@PathVariable long id) {
        messageService.togglePinned(id);
        return ResponseEntity.ok().body(new ApiResponse("Đã đảo ngược trạng thái ghim thông báo", 200));
    }

    @PostMapping("/messages/recipe/rejection")
    public ResponseEntity<ApiResponse> sendNotificationToViolatedUser(
            @RequestParam long accountId,
            @RequestParam long messageId,
            @RequestParam long recipeId) {
        adminTaskService.rejectAnRecipe(new AccountMessageRequest(accountId, messageId, recipeId));
        return ResponseEntity.ok().body(new ApiResponse("Đã gửi thông báo tới tác giả", 200));
    }

    @PostMapping("/{recipeId}/approval")
    public ResponseEntity<ApiResponse> approveRecipe(@PathVariable long recipeId) {
        adminTaskService.approveAnRecipe(recipeId);
        return ResponseEntity.ok().body(new ApiResponse("Đã phê duyệt bài viết", 200));
    }

    // Quan li tai khoan
    @GetMapping(value = "/accounts")
    public ResponseEntity<List<AccountResponse>> accounts() {
        return accountService.getAllAccounts();
    }

    @PutMapping(value = "/accounts/{accountId}")
    public ResponseEntity<ApiResponse> toggleAccountStatus(@PathVariable long accountId) {
        return accountService.togggleAccountStatus(accountId);
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountPageableResponse> getAccountsPageable(@RequestBody AccountsPageableRequest request) {
        return ResponseEntity.ok()
                .body(adminTaskService.getAccountPageableResponse(request.getPage(), request.getSize()));
    }

    // Phân tích
    @GetMapping("/analytics/accounts/registrations")
    public ResponseEntity<List<Long>> getMonthlyRegisteredAccountsOfYear(
            @RequestParam("year") int year) {
        List<Long> result = adminTaskService.getMonthlyRegisteredAccountsOfYear(year);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/accounts/loves")
    public ResponseEntity<List<Long>> getMonthlyRecipeLovesOfYear(
            @RequestParam("year") int year) {
        List<Long> result = adminTaskService.getMonthlyRecipeLovesOfYear(year);
        return ResponseEntity.ok(result);
    }

}

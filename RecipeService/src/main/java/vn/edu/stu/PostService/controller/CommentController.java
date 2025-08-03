package vn.edu.stu.PostService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import vn.edu.stu.PostService.request.CommentRequest;
import vn.edu.stu.PostService.response.ApiResponse;
import vn.edu.stu.PostService.response.CommentResponse;
import vn.edu.stu.PostService.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/recipes")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @PostMapping("/{recipeId}/comments")
    public ResponseEntity<ApiResponse> addComment(@RequestBody CommentRequest commentRequest,
            @PathVariable long recipeId) {
         commentService.createComment(commentRequest, recipeId);
        return ResponseEntity.ok().body(new ApiResponse("Da tao comment",200));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable long commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity.ok().body(new ApiResponse("Đã xóa bình luận", 200));
    }

    @GetMapping("/{recipeId}/comments")
    public ResponseEntity<List<CommentResponse>> getRecipesComments(@PathVariable long recipeId,
            @CookieValue("accessToken") String accessToken) {
        List<CommentResponse> result = commentService.getRecipeComments(recipeId, accessToken);
        if (result.isEmpty()) {
            logger.info("Comment responses rong!");
            return ResponseEntity.ok().body(List.of());
        }
        return ResponseEntity.ok().body(result);
    }
}

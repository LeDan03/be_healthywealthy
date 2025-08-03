package vn.edu.stu.PostService.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.dto.CommenterDto;
import vn.edu.stu.PostService.model.Comment;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.repository.CommentRepo;
import vn.edu.stu.PostService.request.CommentRequest;
import vn.edu.stu.PostService.response.CommentResponse;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final CommentRepo commentRepo;
    private static final Logger logger = LoggerFactory.getLogger(CommentMapper.class);
    // private static final int MAX_DEPTH = 3;

    public Comment toComment(CommentRequest commentRequest, Recipe recipe) {
        Long parentId = commentRequest.getParentId();
        Comment parent = commentRepo.findById(parentId).orElse(null);

        String imageUrl = "";
        String imagePublicId = "";
        if (commentRequest.getImageDto() != null) {
            imageUrl = Optional.ofNullable(commentRequest.getImageDto().getSecureUrl()).orElse("");
            imagePublicId = Optional.ofNullable(commentRequest.getImageDto().getPublicId()).orElse("");
        }

        return Comment.builder()
                .accountId(commentRequest.getAccountId())
                .recipe(recipe)
                .content(commentRequest.getContent())
                .imageUrl(imageUrl)
                .imagePublicId(imagePublicId)
                .createdAt(Timestamp.from(Instant.now()))
                .parent(parent)
                .replies(new ArrayList<>()) // Khởi tạo mặc định
                .build();
    }

    public CommentResponse toResponse(Comment comment, int depth, Map<Long, CommenterDto> cmterMap) {
        try {
            CommenterDto commenter = cmterMap.getOrDefault(comment.getAccountId(),
                    new CommenterDto(comment.getAccountId(), "You", null));

            return CommentResponse.builder()
                    .id(comment.getId())
                    .accountId(comment.getAccountId())
                    .content(comment.getContent())
                    .recipeId(comment.getRecipe() != null ? comment.getRecipe().getId() : 0)
                    .createdAt(comment.getCreatedAt())
                    .imageUrl(comment.getImageUrl())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : 0)
                    .replies(comment.getReplies() != null
                            ? comment.getReplies().stream()
                                    .map(c -> toResponse(c, depth + 1, cmterMap))
                                    .collect(Collectors.toList())
                            : new ArrayList<>())
                    .commenter(commenter)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to map comment {} to response", comment.getId(), e);
            throw new IllegalStateException("Failed to map comment to response", e);
        }
    }

    public CommentResponse toResponse(Comment comment) {
        try {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .accountId(comment.getAccountId())
                    .content(comment.getContent())
                    .recipeId(comment.getRecipe().getId())
                    .createdAt(comment.getCreatedAt())
                    .imageUrl(comment.getImageUrl())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : 0)
                    .replies(new ArrayList<>())
                    .commenter(new CommenterDto(comment.getAccountId()))
                    .build();
        } catch (Exception e) {
            // TODO: handle exception
            logger.warn("Map comment -> reponse fail");
            return new CommentResponse();
        }
    }

    public List<CommentResponse> toComments(List<Comment> comments, Map<Long, CommenterDto> cmterMap) {
        if (comments == null) {
            logger.warn("Input comments list is null");
            return List.of();
        }
        return comments.stream()
                .map(comment -> toResponse(comment, 0, cmterMap))
                .toList();
    }

    public List<CommentResponse> toComments(List<Comment> comments) {
        return toComments(comments, new HashMap<>());
    }
}
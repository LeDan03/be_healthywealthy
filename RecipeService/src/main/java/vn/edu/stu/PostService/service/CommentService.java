package vn.edu.stu.PostService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.exception.NotFoundException;
import vn.edu.stu.PostService.mapper.CommentMapper;
import vn.edu.stu.PostService.model.Comment;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.repository.CommentRepo;
import vn.edu.stu.PostService.repository.RecipeRepo;
import vn.edu.stu.PostService.request.CommentRequest;
import vn.edu.stu.PostService.response.CommentResponse;
import vn.edu.stu.PostService.client.AccountClient;
import vn.edu.stu.PostService.dto.CommenterDto;
import vn.edu.stu.PostService.exception.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepo commentRepo;
    private final CommentMapper commentMapper;
    private final RecipeRepo recipeRepo;
    private final AccountClient accountClient;
    private final static Logger logger = LoggerFactory.getLogger(CommentService.class);

    public void save(Comment comment) {
        commentRepo.save(comment);
    }

    public List<CommentResponse> findByRecipeId(long recipeId) {
        List<Comment> comments = commentRepo.findByRecipeId(recipeId);
        if (comments.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bình luận nào");
        }
        return commentMapper.toComments(comments);
    }

    public CommentResponse createComment(CommentRequest commentRequest, long recipeId) {
        if (!recipeRepo.existsById(recipeId)) {
            throw new NotFoundException("Công thức không tồn tại!");
        }

        Recipe recipe = recipeRepo.findById(recipeId).get();
        Comment comment = commentMapper.toComment(commentRequest, recipe);
        if (comment.getParent() == null) {
            logger.info("Comment khong co cha");
        } else {
            logger.info("REPLY parent id: {}", comment.getParent().getId());
        }
        commentRepo.save(comment);
        return commentMapper.toResponse(comment);
    }

    public boolean deleteCommentById(long id) {
        return commentRepo.deleteById(id) > 0;
    }

    public List<CommentResponse> getRecipeComments(long recipeId, String accessToken) {
        // Kiểm tra đầu vào
        if (recipeId <= 0) {
            logger.error("Invalid recipeId: {}", recipeId);
            throw new IllegalArgumentException("recipeId phải lớn hơn 0");
        }
        if (accessToken == null || accessToken.isBlank()) {
            logger.error("Access token rỗng hoặc null");
            throw new IllegalArgumentException("Access token không được rỗng");
        }

        // Lấy bình luận từ cơ sở dữ liệu
        List<Comment> comments = commentRepo.findByRecipeId(recipeId);
        if (comments.isEmpty()) {
            logger.info("Không tìm thấy bình luận nào cho recipeId: {}", recipeId);
            return List.of();
        }

        // Lấy danh sách accountId duy nhất
        List<Long> cmtersIds = comments.stream()
                .map(Comment::getAccountId)
                .distinct()
                .toList();

        // Lấy thông tin người bình luận
        List<CommenterDto> commenterDtos;
        try {
            commenterDtos = accountClient.getCommentersDetail(cmtersIds, accessToken);
            if (commenterDtos.size() != cmtersIds.size()) {
                logger.warn("Nhận được {} CommenterDtos, kỳ vọng {}", commenterDtos.size(), cmtersIds.size());
            } else {
                logger.debug("Đã lấy thông tin commenters từ account client", commenterDtos);
            }
        } catch (Exception e) {
            logger.error("Không thể lấy thông tin người bình luận cho recipeId: {}", recipeId, e);
            throw new InternalServerErrorException("Không thể lấy thông tin người bình luận", e);
        }
        // Ánh xạ CommenterDto theo accountId
        Map<Long, CommenterDto> cmterMap = commenterDtos.stream()
                .collect(Collectors.toMap(CommenterDto::getId, Function.identity()));
        // Lọc bình luận gốc trước khi ánh xạ
        List<Comment> rootComments = comments.stream()
                .filter(c -> c.getParent() == null || c.getParent().getId() == 0)
                .toList();
        // Chuyển đổi bình luận gốc sang CommentResponse
        List<CommentResponse> responses = commentMapper.toComments(rootComments, cmterMap);
        logger.info("Số lượng bình luận gốc: {}", responses.size());
        // Sắp xếp bình luận gốc theo thời gian (mới nhất trước)
        return responses;
    }
}

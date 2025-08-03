package vn.edu.stu.PostService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.stu.PostService.client.clientResponse.AccountResponse;
import vn.edu.stu.PostService.response.CommentResponse;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentWithAccountDto {
    private CommentResponse commentResponse;
    private AccountResponse accountResponse;
}

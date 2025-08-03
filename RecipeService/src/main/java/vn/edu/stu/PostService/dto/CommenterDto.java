package vn.edu.stu.PostService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommenterDto {
    private long id;
    private String username;
    private String avatarUrl;

    public CommenterDto(long accountId){
        this.id = accountId;
        this.username = "";
        this.avatarUrl = "";
    }
}

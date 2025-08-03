package vn.edu.stu.PostService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateImageDto {
    private String secureUrl;
    private String publicId;
}

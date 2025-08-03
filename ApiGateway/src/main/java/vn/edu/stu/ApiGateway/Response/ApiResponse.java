package vn.edu.stu.ApiGateway.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    public ApiResponse(String message, int status) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}

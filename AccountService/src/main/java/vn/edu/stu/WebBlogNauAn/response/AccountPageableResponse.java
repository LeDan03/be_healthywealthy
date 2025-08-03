package vn.edu.stu.WebBlogNauAn.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountPageableResponse {
    private List<AccountResponse> accounts;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private boolean last;
}

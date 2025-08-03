package vn.edu.stu.WebBlogNauAn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountsPageableRequest {
    private int size;
    private int page;
}

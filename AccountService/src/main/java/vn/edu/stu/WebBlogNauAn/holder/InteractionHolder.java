package vn.edu.stu.WebBlogNauAn.holder;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class InteractionHolder {
    private final ConcurrentHashMap<String , BlockingQueue<String>> responseMap  = new ConcurrentHashMap<>();
//ConcurrentHashMap: cho phép truy cập đồng thời an toàn từ nhiều thread
//    responseMap: là một bản đồ (Map) ánh xạ key duy nhất của từng yêu cầu tới một BlockingQueue.
//    Mỗi yêu cầu/response sẽ có 1 hàng đợi riêng biệt, đảm bảo không bị lẫn lộn giữa các người dùng hoặc request khác nhau.

//    Đặt response vào hàng đợi tương ứng với key.
    public void putResponse(String key ,String response) {
        responseMap.computeIfAbsent(key, k-> new ArrayBlockingQueue<>(1)).add(response);
//        computeIfAbsent: Nếu chưa có hàng đợi cho key, sẽ tạo ArrayBlockingQueue<>(1) (có sức chứa 1).
    }

    public String waitResponse(String key, long timeoutMillis) throws InterruptedException {
//        Lấy hoặc tạo mới BlockingQueue theo key
        BlockingQueue<String> responseQueue = responseMap.computeIfAbsent(key, k-> new ArrayBlockingQueue<>(1));
//        Trả về null nếu hết thời gian chờ mà không có gì.
        String result =responseQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
//        Sau khi xử lý xong, xóa hàng đợi để tránh giữ bộ nhớ không cần thiết (memory leak).
        responseMap.remove(key);
        return result;
    }
}

package vn.edu.stu.WebBlogNauAn.holder;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class RecipeHolder {
    private final BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);

    public void putResponse(String response) {
        responseQueue.add(response);
    }

    public String waitForResponse(long timeoutMillis) throws InterruptedException {
        return responseQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}

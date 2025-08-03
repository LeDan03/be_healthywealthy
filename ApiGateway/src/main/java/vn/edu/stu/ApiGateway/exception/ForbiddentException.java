package vn.edu.stu.ApiGateway.exception;

public class ForbiddentException extends RuntimeException {
    public ForbiddentException(String message) {
        super(message);
    }
}

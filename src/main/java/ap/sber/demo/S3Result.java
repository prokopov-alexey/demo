package ap.sber.demo;

import lombok.Data;

@Data
public class S3Result {
    public S3Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    private boolean success;
    private String message;
}

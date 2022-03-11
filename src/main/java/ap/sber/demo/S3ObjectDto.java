package ap.sber.demo;

import java.util.Date;
import lombok.Data;

@Data
public class S3ObjectDto {

    public S3ObjectDto(String key, Date lastModified) {
        this.key = key;
        this.lastModified = lastModified;
    }
    
    private String key;
    
    private Date lastModified;
}

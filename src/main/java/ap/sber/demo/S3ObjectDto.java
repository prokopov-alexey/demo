package ap.sber.demo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3ObjectDto {
    private String key;
    
    private Date lastModified;
}

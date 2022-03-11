package ap.sber.demo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class S3ObjectListDto {

    public S3ObjectListDto(boolean truncated) {
        this.truncated = truncated;
        this.list = new ArrayList<>();
    }
    
    private final List<S3ObjectDto> list;
    
    private boolean truncated;
}

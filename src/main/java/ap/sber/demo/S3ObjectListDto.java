package ap.sber.demo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3ObjectListDto {
    private final List<S3ObjectDto> list = new ArrayList<>();
    
    private boolean truncated;

    public int getCount()
    {
        return list.size();
    }
}

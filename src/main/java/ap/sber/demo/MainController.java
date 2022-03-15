package ap.sber.demo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {
    
    public final int BATCH_SIZE = 2;
    
    private final AmazonS3 client;
    
    @Value("${s3.bucketName}")
    private String bucket;

    @Value("${s3.directory}")
    private String directory;

    @Value("${s3.maxAge}")
    private int maxAge;
    
    @GetMapping("/list")
    public S3ObjectListDto list(@RequestParam(defaultValue = "100", required = false) int maxKeys, @RequestParam(required = false) String startKey)
    {
        ListObjectsV2Result result = getList(startKey, maxKeys);
        
        return toDto(result);
    }

    @PostMapping("/clean")
    public S3Result clean()
    {
        int count = 0;
        
        ListObjectsV2Result result = getList(null, BATCH_SIZE);
        
        count += cleanList(result.getObjectSummaries());
        
        while (result.isTruncated() && !result.getObjectSummaries().isEmpty()) {
            String lastKey = result.getObjectSummaries().get(result.getObjectSummaries().size() - 1).getKey();
            result = getList(lastKey, BATCH_SIZE);
            count += cleanList(result.getObjectSummaries());
        }
        
        return new S3Result(true, "Deleted: " + count);
    }
    
    private int cleanList(List<S3ObjectSummary> list) {
        int count = 0;
        for(S3ObjectSummary summary: list) {
            if (!inFolder(summary.getKey())) {
                continue;
            }
            
            if (summary.getLastModified().compareTo(Date.from(Instant.now().minus(maxAge, ChronoUnit.MINUTES))) < 0 ) {
                client.deleteObject(bucket, summary.getKey());
                count++;
            }
        }
        
        return count;
    }

    
    @PostMapping("/create")
    public S3Result create()
    {
        String id = getNewObjectName();
        
        String data = "1";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setExpirationTime(Date.from(Instant.now().plus(maxAge, ChronoUnit.MINUTES)));
        
        try {
            client.putObject(bucket, wrapToFolder(id), new ByteArrayInputStream(data.getBytes()), metadata);
        } catch (AmazonServiceException e) {
            return new S3Result(false, e.getErrorMessage());
        }
        
        return new S3Result(true, "Ok");
    }
    
    public ListObjectsV2Result getList(String startKey, int maxKeys)
    {
        ListObjectsV2Request request = new ListObjectsV2Request();
        
        request.setBucketName(bucket);
        request.setPrefix(directory + "/");
        request.setMaxKeys(maxKeys);
        if (startKey != null) {
            request.setStartAfter(startKey);
        }
        
        return client.listObjectsV2(request);
    }
    
    private S3ObjectListDto toDto(ListObjectsV2Result result)
    {
        S3ObjectListDto list = new S3ObjectListDto(result.isTruncated());
        
        result.getObjectSummaries().forEach((S3ObjectSummary summary) -> {
            list.getList().add(new S3ObjectDto(summary.getKey(), summary.getLastModified()));
        });
        
        return list;
    }
    
    public String wrapToFolder(String id)
    {
        return directory + "/" + id;
    }
    
    public boolean inFolder(String key)
    {
        return key.indexOf(directory + "/") == 0;
    }
    
    public String getNewObjectName()
    {
        int randomId = (int)(Math.random() * 10000);
        
        return "dev-" + randomId;
    }
    
}

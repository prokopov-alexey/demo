package ap.sber.demo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    
    @Autowired
    private AmazonS3 client;
    
    @Value("${s3.bucket}")
    private String bucket;

    @Value("${s3.directory}")
    private String directory;
    
    @GetMapping("/list")
    public String main()
    {
        ObjectListing objects = client.listObjects(bucket);
        
        List<S3ObjectSummary> list = objects.getObjectSummaries();
        
        String response = "";
        
        while (true) {
            response += showList(list);
    
            if (!objects.isTruncated()) {
                break;
            }
            
            objects = client.listNextBatchOfObjects(objects);
        }
        
        return response;
    }

    @PostMapping("/clean")
    public String clean()
    {
        ObjectListing objects = client.listObjects(bucket);
        
        List<S3ObjectSummary> list = objects.getObjectSummaries();
        
        while (true) {
            cleanList(list);
    
            if (!objects.isTruncated()) {
                break;
            }
            
            objects = client.listNextBatchOfObjects(objects);
        }
        
        return "Ok";
    }
    
    private String showList(List<S3ObjectSummary> list) {
        String response = "";
        
        for(S3ObjectSummary summary: list) {
            S3Object object = client.getObject(bucket, summary.getKey());
            response += "Key=" + object.getKey();
            if (object.getObjectMetadata() != null) {
                response += "; ExpirationTime=" + object.getObjectMetadata().getExpirationTime();
            }
            
            response += "\n";
        }
        
        return response;
    }

    private void cleanList(List<S3ObjectSummary> list) {
        for(S3ObjectSummary summary: list) {
            S3Object object = client.getObject(bucket, summary.getKey());
            if (!inFolder(object.getKey())) {
                continue;
            }
            
            if (object.getObjectMetadata() == null || object.getObjectMetadata().getExpirationTime() == null) {
                continue;
            }
            
            if (object.getObjectMetadata().getExpirationTime().compareTo(Date.from(Instant.now())) < 0 ) {
                client.deleteObject(bucket, object.getKey());
            }
        }
    }

    
    @PostMapping("/create")
    public String save()
    {
        String id = getNewObjectName();
        
        String data = "ABC";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setExpirationTime(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)));
//        metadata.setExpirationTimeRuleId("deleteExpired");
        
        try {
            client.putObject(bucket, wrapToFolder(id), new ByteArrayInputStream(data.getBytes()), metadata);
        } catch (AmazonServiceException e) {
            return e.getErrorMessage();
        }
        
        return "Ok";
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

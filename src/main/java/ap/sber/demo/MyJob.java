package ap.sber.demo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class MyJob extends QuartzJobBean implements Job {

//    @Override
//    public void execute(JobExecutionContext jec) throws JobExecutionException {
//        System.out.println("executed");
//    }
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        System.out.println("MyJob executed");
    }
    
}

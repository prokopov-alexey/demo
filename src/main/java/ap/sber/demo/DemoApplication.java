package ap.sber.demo;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
//        Scheduler scheduler = ctx.getBean(Scheduler.class);
////        JobBuilder jb = ctx.getBean(JobBuilder.class);
//
//        JobDetail job = JobBuilder.newJob().ofType(MyJob.class).build();
//
//        Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInSeconds(20)
//                .repeatForever()).build();
//
//        try {
//            scheduler.scheduleJob(job, trigger);
//            
//            scheduler.start();
//        } catch (SchedulerException e) {
//
//        }

    }

}

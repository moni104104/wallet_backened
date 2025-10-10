package com.icsd.serviceImpl;

import com.icsd.schedulers.WelcomeEmailJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class DynamicSchedulerService {

    @Autowired
    Scheduler scheduler;

    @Value("${scheduler.welcome.email.delay-seconds}")
    private long delayTime;

    public void scheduleWelcomeEmail(String emailId, String firstName) throws SchedulerException {
        log.info("inside dynamic scheduler service   >>>> schedule welcome email");

        String jobId = UUID.randomUUID().toString();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("emailId", emailId);
        dataMap.put("firstName", firstName);
        log.info("put the data into datamap");

        JobDetail jobDetail = JobBuilder.newJob(WelcomeEmailJob.class)
                .withIdentity("Welcome" + jobId, "welcomeGroup")
                .usingJobData(dataMap)
                .build();
        Date triggerTime = Date.from(Instant.now().plusSeconds(delayTime));
        log.info(" calculate the time");

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("Welcome" + jobId, "welcomeGroup")
                .startAt(triggerTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        log.info("schedule job method run ");
        scheduler.scheduleJob(jobDetail, trigger);
    }
}

package com.icsd.schedulers;

import com.icsd.dto.common.messages;
import com.icsd.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;


@Slf4j
public class WelcomeEmailJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {

            log.info("inside schedulers>>> welcomeEmailJob");
            JobDataMap dataMap = context.getMergedJobDataMap();
            String emailId = dataMap.getString("emailId");
            String firstName = dataMap.getString("firstName");
            log.info("Before application context");

            ApplicationContext applicationContext =(ApplicationContext) context.getScheduler().getContext().get("applicationContext");
        EmailService emailService= applicationContext.getBean(EmailService.class);
            log.info("After application context");

        String subject= messages.EMAIL_SUBJECT;
        String body= "HELLO! " +firstName +", Welcome to our platform.";
            log.info("call emailservice.send email method");

            emailService.sendEmail(emailId,subject,body);
        }catch(Exception e){
            throw new JobExecutionException(e);

        }
    }
}

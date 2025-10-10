package com.icsd.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class QuartzConfig {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() throws SchedulerException{
        log.info("inside quartz configuration");
        scheduler.getContext().put("applicationContext",applicationContext);
    }
}

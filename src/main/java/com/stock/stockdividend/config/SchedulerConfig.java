package com.stock.stockdividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler poolTaskScheduler = new ThreadPoolTaskScheduler();

        // 코어 수
        int coreCnt = Runtime.getRuntime().availableProcessors();
        poolTaskScheduler.setPoolSize(coreCnt);
        poolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(poolTaskScheduler);
    }
}

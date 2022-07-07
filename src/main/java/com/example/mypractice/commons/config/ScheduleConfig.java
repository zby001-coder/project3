package com.example.mypractice.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 配置schedule线程池，定时执行某些任务
 *
 * @author 张贝易
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //配置一个自己的线程池，让schedule执行得更加顺滑
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(2, runnable -> {
            return new Thread(runnable, "Schedule Thread");
        });
        taskRegistrar.setScheduler(threadPoolExecutor);
    }
}

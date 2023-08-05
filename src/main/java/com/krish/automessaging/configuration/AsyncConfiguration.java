package com.krish.automessaging.configuration;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * The Class AsyncConfiguration.
 *
 * Create {@link Async} configuration and use it for any operations be done on a parallel thread
 */
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    /**
     * Thread pool task executor.
     *
     * @return the executor
     *
     *         The {@link #threadPoolTaskExecutor()} creates a {@link ThreadPoolTaskExecutor} which defines the
     *         configuration of the thread to be used
     */
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(500);
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(1000);
        executor.setThreadNamePrefix("TaskExecutorThread - ");
        executor.initialize();
        return executor;
    }

    /**
     * Gets the async executor.
     *
     * @return the async executor
     */
    @Override
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    /**
     * Gets the async uncaught exception handler.
     *
     * @return the async uncaught exception handler
     *
     *         Catch any {@link Async} exceptions from here
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }
}

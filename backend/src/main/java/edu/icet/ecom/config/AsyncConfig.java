package edu.icet.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // This wakes up the Async engine!
public class AsyncConfig {

    // We are creating a custom team of background threads
    @Bean(name = "backgroundTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Always keep 2 workers ready
        executor.setMaxPoolSize(10); // If it gets busy, hire up to 10
        executor.setQueueCapacity(100); // If more than 10 tasks come in, put them in a waiting line
        executor.setThreadNamePrefix("AsyncWorker-"); // This helps us read the logs!
        executor.initialize();
        return executor;
    }
}
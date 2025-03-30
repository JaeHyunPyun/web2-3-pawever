package com.pawever.server.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor customAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);    // 기본 스레드수
        executor.setMaxPoolSize(10);    // 최대 스레드수
        executor.setThreadNamePrefix("Async-");     // 스레드 prefix
        executor.initialize();
        return executor;
    }
}

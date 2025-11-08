package com.example.bookmark.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 *
 * @EnableAsync: 비동기 메서드 실행 활성화
 * - @Async가 붙은 메서드를 별도 스레드풀에서 실행
 * - 이벤트 리스너를 비동기로 처리하여 메인 트랜잭션과 분리
 *
 * Thread Pool 설정은 application.properties에서 외부화
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.executor.corePoolSize:2}")
    private int corePoolSize;

    @Value("${async.executor.maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${async.executor.queueCapacity:100}")
    private int queueCapacity;

    @Value("${async.executor.threadNamePrefix:Async-}")
    private String threadNamePrefix;

    /**
     * 비동기 작업을 위한 Thread Pool 설정
     *
     * 설정값은 application.properties에서 읽어옴
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        log.info("Async Executor configured: core={}, max={}, queue={}, prefix={}",
                corePoolSize, maxPoolSize, queueCapacity, threadNamePrefix);

        return executor;
    }

    /**
     * 비동기 메서드에서 발생한 예외 처리
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async method {} threw exception: {}", method.getName(), ex.getMessage());
            log.error("Exception details:", ex);
        };
    }
}

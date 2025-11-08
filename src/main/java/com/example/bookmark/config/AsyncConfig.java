package com.example.bookmark.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
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
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 비동기 작업을 위한 Thread Pool 설정
     *
     * - corePoolSize: 기본 스레드 수 (2개)
     * - maxPoolSize: 최대 스레드 수 (10개)
     * - queueCapacity: 큐 용량 (100개)
     * - threadNamePrefix: 스레드 이름 접두사 (디버깅 용이)
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
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

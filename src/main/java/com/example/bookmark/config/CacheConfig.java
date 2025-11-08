package com.example.bookmark.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 설정
 *
 * @EnableCaching: Spring Cache Abstraction 활성화
 * - Caffeine 기반 고성능 로컬 캐시 사용
 * - 설정은 application.properties에서 외부화
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Value("${cache.caffeine.spec:expireAfterWrite=5m,maximumSize=1000,recordStats}")
    private String caffeineSpec;

    /**
     * Caffeine Cache Manager 설정
     *
     * 캐시 정책은 application.properties의 cache.caffeine.spec에서 읽어옴
     * 기본값: expireAfterWrite=5m,maximumSize=1000,recordStats
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.from(caffeineSpec));

        log.info("Caffeine Cache configured with spec: {}", caffeineSpec);

        return cacheManager;
    }
}

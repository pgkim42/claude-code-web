package com.example.bookmark.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 캐시 설정
 *
 * @EnableCaching: Spring Cache Abstraction 활성화
 * - Caffeine 기반 고성능 로컬 캐시 사용
 * - TTL 5분, 최대 1000개 엔트리
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine Cache Manager 설정
     *
     * 캐시 정책:
     * - expireAfterWrite(5분): 쓰기 후 5분이 지나면 자동 만료
     * - maximumSize(1000): 최대 1000개 엔트리 저장 (LRU 방식)
     * - recordStats(): 캐시 통계 수집 (히트율, 미스율 등)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats());
        return cacheManager;
    }
}

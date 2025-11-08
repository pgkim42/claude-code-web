package com.example.bookmark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 설정
 *
 * @EnableAspectJAutoProxy: AspectJ 기반 AOP 활성화
 * - proxyTargetClass = true: CGLIB 프록시 사용 (인터페이스 없는 클래스도 프록시 가능)
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
    // AOP 활성화를 위한 설정 클래스
    // Aspect 빈들은 자동으로 감지되어 적용됨
}

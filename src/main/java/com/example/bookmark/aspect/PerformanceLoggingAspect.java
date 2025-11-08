package com.example.bookmark.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 성능 로깅 Aspect
 *
 * 모든 Service 메서드의 실행 시간을 측정하고 로그를 남깁니다.
 * - 1초 이상 걸리면 WARN 레벨로 로그 출력
 * - 1초 미만이면 DEBUG 레벨로 로그 출력
 */
@Aspect
@Component
@Slf4j
public class PerformanceLoggingAspect {

    /**
     * Pointcut: service 패키지 내의 모든 메서드
     */
    @Pointcut("execution(* com.example.bookmark.service..*(..))")
    public void serviceMethods() {}

    /**
     * Around advice: 메서드 실행 전후로 시간 측정
     *
     * @param joinPoint 메서드 실행 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("serviceMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            // 메서드 실행
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 1초 이상 걸리면 WARN, 아니면 DEBUG
            if (executionTime > 1000) {
                log.warn("⚠️ [SLOW] {}.{}() executed in {}ms",
                        className, methodName, executionTime);
            } else {
                log.debug("✅ [PERF] {}.{}() executed in {}ms",
                        className, methodName, executionTime);
            }

            return result;

        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ [ERROR] {}.{}() failed after {}ms",
                    className, methodName, executionTime);
            throw ex;
        }
    }
}

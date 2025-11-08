package com.example.bookmark.aspect;

import com.example.bookmark.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 메서드 호출 로깅 Aspect
 *
 * Resolver 계층의 메서드 호출을 상세하게 로깅합니다.
 * - @Before: 메서드 호출 전 파라미터 로깅
 * - @AfterReturning: 정상 반환 시 결과 로깅
 * - @AfterThrowing: 예외 발생 시 로깅
 */
@Aspect
@Component
@Slf4j
public class MethodLoggingAspect {

    /**
     * Before advice: 메서드 호출 전 로깅
     */
    @Before("execution(* com.example.bookmark.resolver..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        if (args.length > 0) {
            log.info("📥 [CALL] {} with args: {}",
                    methodName, Arrays.toString(args));
        } else {
            log.info("📥 [CALL] {}", methodName);
        }
    }

    /**
     * AfterReturning advice: 정상 반환 시 로깅
     */
    @AfterReturning(
        pointcut = "execution(* com.example.bookmark.resolver..*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();

        if (result != null) {
            String resultType = result.getClass().getSimpleName();
            log.info("📤 [RETURN] {} returned: {} ({})",
                    methodName, LoggingUtils.truncate(result.toString(), 100), resultType);
        } else {
            log.info("📤 [RETURN] {} returned: null", methodName);
        }
    }

    /**
     * AfterThrowing advice: 예외 발생 시 로깅
     */
    @AfterThrowing(
        pointcut = "execution(* com.example.bookmark.resolver..*(..))",
        throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("💥 [EXCEPTION] {} threw {}: {}",
                methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }
}

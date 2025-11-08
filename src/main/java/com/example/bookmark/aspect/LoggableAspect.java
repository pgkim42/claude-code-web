package com.example.bookmark.aspect;

import com.example.bookmark.aspect.annotation.Loggable;
import com.example.bookmark.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Loggable 어노테이션 처리 Aspect
 *
 * @Loggable이 붙은 메서드의 상세한 정보를 로깅합니다.
 */
@Aspect
@Component
@Slf4j
public class LoggableAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * @Loggable 어노테이션이 붙은 메서드 처리
     */
    @Around("@annotation(loggable)")
    public Object logMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String customMessage = loggable.value();

        String timestamp = LocalDateTime.now().format(FORMATTER);

        log.info("╔═══════════════════════════════════════════════════════════════");
        log.info("║ 🔍 [@Loggable] Method Execution Started");
        log.info("║ Time: {}", timestamp);
        log.info("║ Class: {}", className);
        log.info("║ Method: {}", methodName);

        if (!customMessage.isEmpty()) {
            log.info("║ Description: {}", customMessage);
        }

        // 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.info("║ Parameters:");
            String[] paramNames = signature.getParameterNames();
            for (int i = 0; i < args.length; i++) {
                log.info("║   - {}: {}", paramNames[i], args[i]);
            }
        }

        long startTime = System.currentTimeMillis();

        try {
            // 메서드 실행
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("║ ✅ Execution Successful");
            log.info("║ Execution Time: {}ms", executionTime);
            if (result != null) {
                log.info("║ Return Type: {}", result.getClass().getSimpleName());
                log.info("║ Return Value: {}", LoggingUtils.truncate(result.toString(), 200));
            } else {
                log.info("║ Return Value: null");
            }
            log.info("╚═══════════════════════════════════════════════════════════════");

            return result;

        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("║ ❌ Execution Failed");
            log.error("║ Execution Time: {}ms", executionTime);
            log.error("║ Exception Type: {}", ex.getClass().getSimpleName());
            log.error("║ Exception Message: {}", ex.getMessage());
            log.error("╚═══════════════════════════════════════════════════════════════");

            throw ex;
        }
    }
}

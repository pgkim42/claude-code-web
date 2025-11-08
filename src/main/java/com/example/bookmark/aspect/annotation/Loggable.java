package com.example.bookmark.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 로깅을 위한 커스텀 어노테이션
 *
 * 이 어노테이션이 붙은 메서드는 상세한 로깅이 자동으로 적용됩니다.
 * - 메서드 호출 시각
 * - 파라미터 값
 * - 반환 값
 * - 실행 시간
 *
 * 사용 예시:
 * <pre>
 * {@code @Loggable}
 * public Bookmark createBookmark(...) {
 *     // ...
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * 로그 메시지에 포함할 커스텀 설명
     */
    String value() default "";
}

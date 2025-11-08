package com.example.bookmark.util;

/**
 * 로깅 관련 유틸리티 메서드 모음
 */
public class LoggingUtils {

    private LoggingUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * 문자열을 지정된 최대 길이로 자르고 "..." 접미사 추가
     *
     * @param str 원본 문자열
     * @param maxLength 최대 길이
     * @return 잘린 문자열
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return "null";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}

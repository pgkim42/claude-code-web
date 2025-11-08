package com.example.bookmark.event.listener;

import com.example.bookmark.event.domain.BookmarkCreatedEvent;
import com.example.bookmark.event.domain.BookmarkDeletedEvent;
import com.example.bookmark.event.domain.BookmarkUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 북마크 이벤트 리스너 (통계 업데이트용)
 *
 * @TransactionalEventListener를 사용하여:
 * - 트랜잭션이 정상적으로 커밋된 후에만 이벤트 처리
 * - 롤백 시 이벤트가 발행되지 않음 (데이터 정합성 보장)
 *
 * @Async를 사용하여:
 * - 메인 트랜잭션과 분리된 별도 스레드에서 실행
 * - 통계 계산이 느려도 사용자 응답 시간에 영향 없음
 */
@Component
@Slf4j
public class StatisticsEventListener {

    /**
     * 북마크 생성 이벤트 처리
     *
     * 실제 통계 업데이트 로직은 여기에 구현 가능:
     * - 실시간 대시보드 업데이트
     * - 통계 집계 테이블 갱신
     * - 알림 전송
     * - 감사 로그 기록
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleBookmarkCreated(BookmarkCreatedEvent event) {
        log.info("📊 [EVENT] Bookmark created: id={}, title={}",
                event.getBookmarkId(), event.getTitle());

        // TODO: 실제 통계 업데이트 로직
        // - 카테고리별 북마크 수 증가
        // - 전체 북마크 수 증가
        // - 즐겨찾기 수 증가 (if favorite)
        // - 레이팅 평균 재계산 (if rating exists)
    }

    /**
     * 북마크 수정 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleBookmarkUpdated(BookmarkUpdatedEvent event) {
        log.info("📊 [EVENT] Bookmark updated: id={}, title={}",
                event.getBookmarkId(), event.getTitle());

        // TODO: 실제 통계 업데이트 로직
        // - 카테고리 변경 시 카운트 재조정
        // - 즐겨찾기 변경 시 카운트 재조정
        // - 레이팅 변경 시 평균 재계산
    }

    /**
     * 북마크 삭제 이벤트 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleBookmarkDeleted(BookmarkDeletedEvent event) {
        log.info("📊 [EVENT] Bookmark deleted: id={}", event.getBookmarkId());

        // TODO: 실제 통계 업데이트 로직
        // - 전체 북마크 수 감소
        // - 카테고리별 북마크 수 감소
        // - 즐겨찾기 수 감소 (if was favorite)
        // - 레이팅 평균 재계산
    }
}

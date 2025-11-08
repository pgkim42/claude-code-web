package com.example.bookmark.event.domain;

import com.example.bookmark.model.Bookmark;

/**
 * Domain Event: Bookmark 생성 시 발행
 *
 * 이벤트 기반 아키텍처를 통해:
 * - 서비스 간 결합도 감소
 * - 통계 업데이트, 알림 전송 등 부가 작업 분리
 * - 비동기 처리 가능
 */
public class BookmarkCreatedEvent extends AbstractBookmarkPayloadEvent {

    public BookmarkCreatedEvent(Object source, Bookmark bookmark) {
        super(source, bookmark);
    }
}

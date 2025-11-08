package com.example.bookmark.event.domain;

import com.example.bookmark.model.Bookmark;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain Event: Bookmark 생성 시 발행
 *
 * 이벤트 기반 아키텍처를 통해:
 * - 서비스 간 결합도 감소
 * - 통계 업데이트, 알림 전송 등 부가 작업 분리
 * - 비동기 처리 가능
 */
@Getter
public class BookmarkCreatedEvent extends ApplicationEvent {

    private final Long bookmarkId;
    private final String title;
    private final String url;
    private final Long categoryId;
    private final Boolean isFavorite;
    private final Integer rating;

    public BookmarkCreatedEvent(Object source, Bookmark bookmark) {
        super(source);
        this.bookmarkId = bookmark.getId();
        this.title = bookmark.getTitle();
        this.url = bookmark.getUrl();
        this.categoryId = bookmark.getCategory() != null ? bookmark.getCategory().getId() : null;
        this.isFavorite = bookmark.getIsFavorite();
        this.rating = bookmark.getRating();
    }
}

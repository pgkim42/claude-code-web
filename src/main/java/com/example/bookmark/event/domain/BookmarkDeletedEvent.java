package com.example.bookmark.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain Event: Bookmark 삭제 시 발행
 *
 * 삭제된 엔티티는 더 이상 존재하지 않으므로
 * ID와 필요한 최소 정보만 포함합니다.
 */
@Getter
public class BookmarkDeletedEvent extends ApplicationEvent {

    private final Long bookmarkId;

    public BookmarkDeletedEvent(Object source, Long bookmarkId) {
        super(source);
        this.bookmarkId = bookmarkId;
    }
}

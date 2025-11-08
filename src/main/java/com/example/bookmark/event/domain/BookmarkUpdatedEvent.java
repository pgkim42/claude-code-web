package com.example.bookmark.event.domain;

import com.example.bookmark.model.Bookmark;

/**
 * Domain Event: Bookmark 수정 시 발행
 *
 * 변경된 필드에 대한 정보를 포함하여
 * 이벤트 리스너가 적절한 후속 작업을 수행할 수 있도록 합니다.
 */
public class BookmarkUpdatedEvent extends AbstractBookmarkPayloadEvent {

    public BookmarkUpdatedEvent(Object source, Bookmark bookmark) {
        super(source, bookmark);
    }
}

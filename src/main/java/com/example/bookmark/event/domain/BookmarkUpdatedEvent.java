package com.example.bookmark.event.domain;

import com.example.bookmark.model.Bookmark;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain Event: Bookmark 수정 시 발행
 *
 * 변경된 필드에 대한 정보를 포함하여
 * 이벤트 리스너가 적절한 후속 작업을 수행할 수 있도록 합니다.
 */
@Getter
public class BookmarkUpdatedEvent extends ApplicationEvent {

    private final Long bookmarkId;
    private final String title;
    private final String url;
    private final Long categoryId;
    private final Boolean isFavorite;
    private final Integer rating;

    public BookmarkUpdatedEvent(Object source, Bookmark bookmark) {
        super(source);
        this.bookmarkId = bookmark.getId();
        this.title = bookmark.getTitle();
        this.url = bookmark.getUrl();
        this.categoryId = bookmark.getCategory() != null ? bookmark.getCategory().getId() : null;
        this.isFavorite = bookmark.getIsFavorite();
        this.rating = bookmark.getRating();
    }
}

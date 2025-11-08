package com.example.bookmark.event.domain;

import com.example.bookmark.model.Bookmark;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Bookmark payload를 포함하는 도메인 이벤트의 추상 기본 클래스
 *
 * Created/Updated 이벤트에서 공통으로 사용하는 필드와 로직을 추출하여
 * 코드 중복을 제거합니다.
 */
@Getter
public abstract class AbstractBookmarkPayloadEvent extends ApplicationEvent {

    private final Long bookmarkId;
    private final String title;
    private final String url;
    private final Long categoryId;
    private final Boolean isFavorite;
    private final Integer rating;

    protected AbstractBookmarkPayloadEvent(Object source, Bookmark bookmark) {
        super(source);
        this.bookmarkId = bookmark.getId();
        this.title = bookmark.getTitle();
        this.url = bookmark.getUrl();
        this.categoryId = bookmark.getCategory() != null ? bookmark.getCategory().getId() : null;
        this.isFavorite = bookmark.getIsFavorite();
        this.rating = bookmark.getRating();
    }
}

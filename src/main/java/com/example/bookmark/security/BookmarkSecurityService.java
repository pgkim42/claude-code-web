package com.example.bookmark.security;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.User;
import com.example.bookmark.model.UserRole;
import com.example.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Security service for bookmark ownership validation
 * Used in @PreAuthorize and @PostAuthorize expressions
 */
@Service("bookmarkSecurity")
@RequiredArgsConstructor
public class BookmarkSecurityService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Check if current user is the owner of the bookmark
     * @param bookmarkId the bookmark ID
     * @return true if current user is owner or admin
     */
    public boolean isOwner(Long bookmarkId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get current user
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        // Admin can access everything
        if (userDetails.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        // Check ownership
        return bookmarkRepository.findById(bookmarkId)
                .map(bookmark -> bookmark.getUser() != null &&
                                bookmark.getUser().getId().equals(userDetails.getUserId()))
                .orElse(false);
    }

    /**
     * Check if current user can view the bookmark
     * (public bookmark or owned by current user)
     * @param bookmark the bookmark
     * @return true if can view
     */
    public boolean canView(Bookmark bookmark) {
        if (bookmark == null) {
            return false;
        }

        // Public bookmarks are viewable by anyone
        if (Boolean.TRUE.equals(bookmark.getIsPublic())) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get current user
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        // Admin can view everything
        if (userDetails.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        // Check ownership for private bookmarks
        return bookmark.getUser() != null &&
               bookmark.getUser().getId().equals(userDetails.getUserId());
    }

    /**
     * Get current authenticated user
     * @return current user or null
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        return null;
    }

    /**
     * Check if current user is authenticated
     * @return true if authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               authentication.getPrincipal() instanceof CustomUserDetails;
    }
}

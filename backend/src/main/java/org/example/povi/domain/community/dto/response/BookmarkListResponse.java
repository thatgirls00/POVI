package org.example.povi.domain.community.dto.response;

import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.CommunityBookmark;

public record BookmarkListResponse(
        Long bookmarkId,
        Long postId,
        String postTitle,
        String postAuthorNickname,
        LocalDateTime postCreatedAt)
{
    public static BookmarkListResponse from(CommunityBookmark bookmark) {
        return new BookmarkListResponse(
                bookmark.getId(),
                bookmark.getCommunityPost().getId(),
                bookmark.getCommunityPost().getTitle(),
                bookmark.getCommunityPost().getUser().getNickname(),
                bookmark.getCommunityPost().getCreatedAt()
        );
    }
}

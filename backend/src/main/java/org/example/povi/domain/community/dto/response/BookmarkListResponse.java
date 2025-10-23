package org.example.povi.domain.community.dto.response;

import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.CommunityBookmark;
import org.example.povi.domain.community.entity.CommunityEmoticon;
import org.example.povi.domain.community.entity.CommunityPost;

public record BookmarkListResponse(
        Long postId,
        String postTitle,
        String content,
        String postAuthorNickname,
        CommunityEmoticon emoticon,
        LocalDateTime postCreatedAt)
{
    public static BookmarkListResponse from(CommunityPost post) {
        return new BookmarkListResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getEmoticon(),
                post.getCreatedAt()
        );
    }
}

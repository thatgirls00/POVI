package org.example.povi.domain.community.dto.response;

import org.example.povi.domain.community.entity.CommunityEmoticon;
import org.example.povi.domain.community.entity.CommunityPost;

import java.time.LocalDateTime;

public record LikeListResponse (
        Long postId,
        String postTitle,
        String content,
        String postAuthorNickname,
        CommunityEmoticon emoticon,
        LocalDateTime postCreatedAt
) {
    public static LikeListResponse from(CommunityPost post) {
        String summaryTitle = post.getTitle();
        String summaryContent = post.getContent();

        if (summaryTitle != null && summaryTitle.length() > 20) {
            summaryTitle = summaryTitle.substring(0, 20) + "...";
        }
        if (summaryContent != null && summaryContent.length() > 20) {
            summaryContent = summaryContent.substring(0, 20) + "...";
        }
        return new LikeListResponse(
                post.getId(),
                summaryTitle,
                summaryContent,
                post.getUser().getNickname(),
                post.getEmoticon(),
                post.getCreatedAt()
        );
    }
}

package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.CommunityEmoticon;
import org.example.povi.domain.community.entity.CommunityPost;

@Schema(description = "북마크한 커뮤니티 게시글 목록 응답 DTO")
public record BookmarkListResponse(
        Long postId,
        String postTitle,
        String content,
        String postAuthorNickname,
        CommunityEmoticon emoticon,
        LocalDateTime postCreatedAt
) {
    public static BookmarkListResponse from(CommunityPost post) {
        String summaryTitle = post.getTitle();
        String summaryContent = post.getContent();

        if (summaryTitle != null && summaryTitle.length() > 20) {
            summaryTitle = summaryTitle.substring(0, 20) + "...";
        }
        if (summaryContent != null && summaryContent.length() > 20) {
            summaryContent = summaryContent.substring(0, 20) + "...";
        }
        return new BookmarkListResponse(
                post.getId(),
                summaryTitle,
                summaryContent,
                post.getUser().getNickname(),
                post.getEmoticon(),
                post.getCreatedAt()
        );
    }
}

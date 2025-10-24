package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.CommunityEmoticon;
import org.example.povi.domain.community.entity.CommunityPost;

@Schema(description = "게시글 목록 응답 DTO")
public record PostListResponse(
        @Schema(description = "게시글 ID", example = "101")
        Long postId,

        @Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
        String title,

        @Schema(description = "게시글 본문 미리보기", example = "집에만 있기 아까운 날씨입니다. 다들 즐거운 하루 보내세요.")
        String content,

        @Schema(description = "작성자 닉네임", example = "행복한개발자")
        String authorNickname,

        @Schema(description = "작성일시")
        LocalDateTime createdAt,

        @Schema(description = "감정 이모티콘", example = "HAPPY")
        CommunityEmoticon emoticon,

        @Schema(description = "좋아요 수", example = "25")
        int likeCount,

        @Schema(description = "댓글 수", example = "10")
        int commentCount


) {
    public static PostListResponse from(CommunityPost post) {
        String summaryContent = post.getContent();
        // 내용이 20자보다 클 경우, "..."을 붙임
        if (summaryContent != null && summaryContent.length() > 20) {
            summaryContent = summaryContent.substring(0, 20) + "...";
        }

        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                summaryContent,
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getEmoticon(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}

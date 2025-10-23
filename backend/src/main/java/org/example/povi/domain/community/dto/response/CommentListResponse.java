package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.Comment;

@Schema(description = "마이페이지 내 댓글 목록 조회를 위한 응답 DTO")
public record CommentListResponse(
        @Schema(description = "댓글 ID")
        Long commentId,

        @Schema(description = "댓글 내용")
        String content,

        @Schema(description = "댓글 작성일")
        LocalDateTime createdAt,

        @Schema(description = "댓글이 달린 원본 게시글 ID")
        Long postId,

        @Schema(description = "댓글이 달린 원본 게시글 제목")
        String postTitle
)
    {
    public static CommentListResponse from(Comment comment) {
        String summaryTitle = comment.getCommunityPost().getTitle();
        String summaryContent = comment.getContent();

        if (summaryTitle != null && summaryTitle.length() > 20) {
            summaryTitle = summaryTitle.substring(0, 20);
        }
        if (summaryContent != null && summaryContent.length() > 20) {
            summaryContent = summaryContent.substring(0, 20) + "...";
        }
        return new CommentListResponse(
                comment.getId(),
                summaryContent,
                comment.getCreatedAt(),
                comment.getCommunityPost().getId(),
                summaryTitle
        );
    } }


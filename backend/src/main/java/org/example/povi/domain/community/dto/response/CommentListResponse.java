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

        @Schema(description = "댓글 좋아요 수")
        int likeCount,

        @Schema(description = "댓글 작성일")
        LocalDateTime createdAt,

        @Schema(description = "댓글이 달린 원본 게시글 ID")
        Long postId,

        @Schema(description = "댓글이 달린 원본 게시글 제목")
        String postTitle
)
    {
    public static CommentListResponse from(Comment comment) {
        return new CommentListResponse(
                comment.getId(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getCommunityPost().getId(),
                comment.getCommunityPost().getTitle()
        );
    } }


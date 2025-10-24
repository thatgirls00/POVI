package org.example.povi.domain.diary.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.comment.entity.DiaryComment;

import java.time.LocalDateTime;

@Schema(description = "다이어리 댓글 수정 응답 DTO")
public record DiaryCommentUpdateRes(
        Long commentId,
        Long postId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime updatedAt
) {
    public static DiaryCommentUpdateRes from(DiaryComment entity) {
        return new DiaryCommentUpdateRes(
                entity.getId(),
                entity.getPost().getId(),
                entity.getAuthor().getId(),
                entity.getAuthor().getNickname(),
                entity.getContent(),
                entity.getUpdatedAt()
        );
    }
}
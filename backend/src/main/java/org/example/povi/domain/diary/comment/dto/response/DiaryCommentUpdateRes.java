package org.example.povi.domain.diary.comment.dto.response;

import org.example.povi.domain.diary.comment.entity.DiaryComment;

import java.time.LocalDateTime;

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
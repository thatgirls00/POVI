package org.example.povi.domain.diary.comment.dto.response;

import org.example.povi.domain.diary.comment.entity.DiaryComment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 응답 DTO
 * - 단건 조회 및 목록 조회 공용으로 사용
 */
public record DiaryCommentRes(
        Long commentId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {

    /**
     * Entity → DTO (단건 변환)
     */
    public static DiaryCommentRes from(DiaryComment entity) {
        return new DiaryCommentRes(
                entity.getId(),
                entity.getAuthor().getId(),
                entity.getAuthor().getNickname(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    /**
     * Entity List → DTO List (목록 변환)
     */
    public static List<DiaryCommentRes> fromList(List<DiaryComment> entities) {
        return entities.stream()
                .map(DiaryCommentRes::from)
                .toList();
    }
}
package org.example.povi.domain.diary.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.comment.entity.DiaryComment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Schema(description = "다이어리 댓글 응답 DTO")
public record DiaryCommentRes(
        Long commentId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt,
        boolean isMine
) {

    /**
     * Entity → DTO (단건 변환)
     */
    public static DiaryCommentRes from(DiaryComment comment, Long currentUserId) {
        Long aid = comment.getAuthor().getId();
        return new DiaryCommentRes(
                comment.getId(),
                aid,
                comment.getAuthor().getNickname(),  // 필드명에 맞게
                comment.getContent(),
                comment.getCreatedAt(),
                Objects.equals(aid, currentUserId)       // null-safe 비교
        );
    }

    /**
     * Entity List → DTO List (목록 변환)
     */
    public static List<DiaryCommentRes> fromList(List<DiaryComment> comments, Long currentUserId) {
        return comments.stream()
                .map(c -> from(c, currentUserId))
                .toList();
    }
}
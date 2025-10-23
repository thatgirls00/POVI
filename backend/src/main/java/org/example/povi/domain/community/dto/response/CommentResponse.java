package org.example.povi.domain.community.dto.response;

import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.Comment;

public record CommentResponse(
        Long commentId,
        String authorName,
        String content,
        int likeCount,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
        );
    }
}

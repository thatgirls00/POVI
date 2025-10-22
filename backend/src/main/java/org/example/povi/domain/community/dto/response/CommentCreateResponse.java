package org.example.povi.domain.community.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.Comment;

@Schema(description = "댓글 작성 완료 응답 DTO")
public record CommentCreateResponse(
        @Schema(description = "생성된 댓글 ID")
        Long commentId,

        @Schema(description = "댓글 내용")
        String content,

        @Schema(description = "작성자 닉네임")
        String authorNickname,

        @Schema(description = "작성 시각")
        LocalDateTime createdAt
) {
    public static CommentCreateResponse from(Comment comment) {
        return new CommentCreateResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt()
        );
    }
}

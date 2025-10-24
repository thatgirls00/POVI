package org.example.povi.domain.diary.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

import java.time.LocalDateTime;

@Schema(description = "다이어리 댓글 작성 응답 DTO")
public record DiaryCommentCreateRes(
        Long commentId,
        Long postId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
    public static DiaryCommentCreateRes from(DiaryComment comment) {
        DiaryPost post = comment.getPost();
        User author = comment.getAuthor();

        return new DiaryCommentCreateRes(
                comment.getId(),
                post.getId(),
                author.getId(),
                author.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
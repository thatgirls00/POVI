package org.example.povi.domain.diary.comment.dto.response;

import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

import java.time.LocalDateTime;

public record DiaryCommentCreateRes(
        Long commentId,
        Long postId,
        Long authorId,
        String content,
        LocalDateTime createdAt
) {
    public static DiaryCommentCreateRes from(DiaryComment diaryComment) {
        DiaryPost diaryPost = diaryComment.getDiaryPost();
        User author = diaryComment.getAuthor();

        return new DiaryCommentCreateRes(
                diaryComment.getId(),
                diaryPost != null ? diaryPost.getId() : null,
                author != null ? author.getId() : null,
                diaryComment.getContent(),
                diaryComment.getCreatedAt()
        );
    }
}
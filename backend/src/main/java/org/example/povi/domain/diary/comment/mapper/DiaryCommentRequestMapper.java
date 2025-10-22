package org.example.povi.domain.diary.comment.mapper;

import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

public final class DiaryCommentRequestMapper {

    private DiaryCommentRequestMapper() {
    }

    /**
     * DiaryCommentCreateReq → DiaryComment 엔티티 변환
     **/
    public static DiaryComment fromCreateRequest(DiaryCommentCreateReq req,
                                                 User author,
                                                 DiaryPost post) {
        return DiaryComment.builder()
                .author(author)
                .post(post)
                .content(req.content().trim())
                .build();
    }
}
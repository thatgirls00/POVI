package org.example.povi.domain.diary.comment.mapper;

import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

public final class DiaryCommentRequestMapper {

    private DiaryCommentRequestMapper() {
    }

    /**
     * DiaryCommentCreateReq → DiaryComment 엔티티 변환
     **/
    public static DiaryComment toEntity(DiaryCommentCreateReq req,
                                                 User author,
                                                 DiaryPost post) {
        return DiaryComment.builder()
                .author(author)
                .post(post)
                .content(req.content().trim())
                .build();
    }

    /**
     * 댓글 수정 요청 적용 (엔티티 변경)
     * - Dirty Checking으로 자동 반영됨
     */
    public static void updateEntity(DiaryComment comment, DiaryCommentUpdateReq req) {
        comment.updateContent(req.content().trim());
    }
}
package org.example.povi.domain.diary.comment.mapper;

import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.global.dto.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public final class DiaryCommentMapper {

    private DiaryCommentMapper() {}

    /**
     * Page<DiaryComment> → PagedResponse<DiaryCommentRes>
     * - 엔티티 페이지를 DTO 페이지 응답 형태로 변환
     */
    public static PagedResponse<DiaryCommentRes> toPagedResponse(Page<DiaryComment> commentPage, Long currentUserId) {
        List<DiaryCommentRes> items = DiaryCommentRes.fromList(commentPage.getContent(), currentUserId);

        return PagedResponse.of(
                items,
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.isFirst(),
                commentPage.isLast()
        );
    }
}
package org.example.povi.domain.diary.like.mapper;

import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes;

public class DiaryPostLikeMapper {

    /** 좋아요 상태 + 카운트 → Response DTO */
    public static DiaryPostLikeRes toResponse(boolean liked, long count) {
        return DiaryPostLikeRes.of(liked, count);
    }
}
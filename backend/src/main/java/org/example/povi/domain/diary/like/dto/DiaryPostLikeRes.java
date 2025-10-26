package org.example.povi.domain.diary.like.dto;

public record DiaryPostLikeRes(
        boolean liked,
        long likeCount
) {
    public static DiaryPostLikeRes of(boolean liked, long likeCount) {
        return new DiaryPostLikeRes(liked, likeCount);
    }
}
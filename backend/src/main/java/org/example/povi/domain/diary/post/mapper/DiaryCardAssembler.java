package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.dto.response.DiaryPostCardRes;
import org.example.povi.domain.diary.post.dto.response.MyDiaryCardRes;
import org.example.povi.domain.diary.post.entity.DiaryPost;

public final class DiaryCardAssembler {

    private static final int PREVIEW_MAX = 100;

    private DiaryCardAssembler() {}

    /** 나의 다이어리 카드 변환 */
    public static MyDiaryCardRes toMyCard(DiaryPost diaryPost) {
        String preview   = DiaryPreviewMapper.buildPreviewText(diaryPost.getContent(), PREVIEW_MAX);
        String thumbnail = DiaryPreviewMapper.firstImageUrl(diaryPost);

        return new MyDiaryCardRes(
                diaryPost.getId(),
                diaryPost.getTitle(),
                preview,
                diaryPost.getMoodEmoji(),
                thumbnail,
                diaryPost.getVisibility(),
                diaryPost.getCreatedAt().toLocalDate()
        );
    }

    /** 공용 카드 변환 (친구 피드 / 모두의 다이어리) */
    public static DiaryPostCardRes toDiaryCard(DiaryPost diaryPost) {
        String preview   = DiaryPreviewMapper.buildPreviewText(diaryPost.getContent(), PREVIEW_MAX);
        String thumbnail = DiaryPreviewMapper.firstImageUrl(diaryPost);

        return new DiaryPostCardRes(
                diaryPost.getId(),
                diaryPost.getUser().getId(),
                diaryPost.getUser().getNickname(),
                diaryPost.getTitle(),
                preview,
                thumbnail,
                diaryPost.getMoodEmoji(),
                diaryPost.getVisibility(),
                diaryPost.getCreatedAt().toLocalDate()
        );
    }
}
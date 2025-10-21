package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.dto.response.MyDiaryListItemRes;
import org.example.povi.domain.diary.post.entity.DiaryPost;

public final class DiaryCardAssembler {

    private DiaryCardAssembler() {}
    public static MyDiaryListItemRes toCard(DiaryPost diaryPost) {
        String previewText = DiaryViewMapper.buildPreviewText(diaryPost.getContent(), 60);
        String thumbnailUrl = DiaryViewMapper.firstImageUrl(diaryPost);

        return new MyDiaryListItemRes(
                diaryPost.getId(),
                diaryPost.getTitle(),
                previewText,
                diaryPost.getMoodEmoji(),
                thumbnailUrl,
                diaryPost.getVisibility(),
                diaryPost.getCreatedAt().toLocalDate()
        );
    }
}

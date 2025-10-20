package org.example.povi.domain.diary.entry.dto.response;


import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.example.povi.domain.diary.entry.entity.DiaryImage;
import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record DiaryDetailRes(
        Long diaryId,
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static DiaryDetailRes from(DiaryEntry diaryEntry) {
        return new DiaryDetailRes(
                diaryEntry.getId(),
                diaryEntry.getTitle(),
                diaryEntry.getContent(),
                diaryEntry.getMoodEmoji(),
                diaryEntry.getVisibility(),
                diaryEntry.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                diaryEntry.getCreatedAt()
        );
    }
}
package org.example.povi.domain.diary.entry.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.example.povi.domain.diary.entry.entity.DiaryImage;
import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DiaryCreateRes {
    private final Long diaryId;
    private final String title;
    private final String content;
    private final MoodEmoji moodEmoji;
    private final Visibility visibility;
    private final List<String> imageUrls;
    private final LocalDateTime createdAt;

    public static DiaryCreateRes from(DiaryEntry diaryEntry) {
        return DiaryCreateRes.builder()
                .diaryId(diaryEntry.getId())
                .title(diaryEntry.getTitle())
                .content(diaryEntry.getContent())
                .moodEmoji(diaryEntry.getMoodEmoji())
                .visibility(diaryEntry.getVisibility())
                .imageUrls(diaryEntry.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList())
                .createdAt(diaryEntry.getCreatedAt())
                .build();
    }
}
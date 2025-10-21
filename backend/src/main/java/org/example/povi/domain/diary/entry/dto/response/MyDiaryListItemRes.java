package org.example.povi.domain.diary.entry.dto.response;

import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;

import java.time.LocalDate;

public record MyDiaryListItemRes(
        Long diaryId,
        String title,
        String preview,
        MoodEmoji moodEmoji,
        String thumbnailUrl,
        Visibility visibility,
        LocalDate createdDate
) { }

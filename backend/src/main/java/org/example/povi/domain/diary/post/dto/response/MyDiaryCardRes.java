package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDate;

public record MyDiaryCardRes(
        Long postId,
        String title,
        String preview,
        MoodEmoji moodEmoji,
        String thumbnailUrl,
        Visibility visibility,
        LocalDate createdDate
) { }

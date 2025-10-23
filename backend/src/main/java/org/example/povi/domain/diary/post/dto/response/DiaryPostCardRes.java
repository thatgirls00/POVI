package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDate;

public record DiaryPostCardRes(
        Long postId,
        Long authorId,
        String authorName,
        String title,
        String preview,
        String thumbnailUrl,
        MoodEmoji moodEmoji,
        Visibility visibility,
        LocalDate createdDate,
        long commentCount

) {
}

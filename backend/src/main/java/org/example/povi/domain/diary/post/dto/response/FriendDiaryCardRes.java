package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;

import java.time.LocalDate;

public record FriendDiaryCardRes(
        Long postId,
        Long authorId,
        String authorName,
        String title,
        String preview,
        String thumbnailUrl,

        MoodEmoji moodEmoji,
        Visibility visibility,
        LocalDate createdDate
) {
}

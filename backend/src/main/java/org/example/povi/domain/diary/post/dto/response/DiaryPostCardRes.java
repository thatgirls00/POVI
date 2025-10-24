package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDate;

@Schema(description = "다이어리 게시글 카드 응답 DTO")
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

package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.entity.DiaryImage;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record DiaryPostUpdateRes(
        Long diaryId,
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DiaryPostUpdateRes from(DiaryPost diaryPost) {
        return new DiaryPostUpdateRes(
                diaryPost.getId(),
                diaryPost.getTitle(),
                diaryPost.getContent(),
                diaryPost.getMoodEmoji(),
                diaryPost.getVisibility(),
                diaryPost.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                diaryPost.getCreatedAt(),
                diaryPost.getUpdatedAt()
        );
    }
}


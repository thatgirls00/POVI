package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.entity.DiaryImage;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record DiaryPostCreateRes(
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static DiaryPostCreateRes from(DiaryPost diaryPost) {
        return new DiaryPostCreateRes(
                diaryPost.getTitle(),
                diaryPost.getContent(),
                diaryPost.getMoodEmoji(),
                diaryPost.getVisibility(),
                diaryPost.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                diaryPost.getCreatedAt()
        );
    }
}
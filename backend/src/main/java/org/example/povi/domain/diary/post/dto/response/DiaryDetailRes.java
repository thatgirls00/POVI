package org.example.povi.domain.diary.post.dto.response;


import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.entity.DiaryImage;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record DiaryDetailRes(
        Long postId,
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static DiaryDetailRes from(DiaryPost post) {
        return new DiaryDetailRes(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMoodEmoji(),
                post.getVisibility(),
                post.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                post.getCreatedAt()
        );
    }
}
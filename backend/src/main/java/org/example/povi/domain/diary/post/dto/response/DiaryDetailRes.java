package org.example.povi.domain.diary.post.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.entity.DiaryImage;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "다이어리 게시글 상세 응답 DTO")
public record DiaryDetailRes(
        Long postId,
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt,
        boolean liked,
        long likeCount,
        long commentCount
) {
    public static DiaryDetailRes of(
            DiaryPost post, boolean liked, long likeCount, long commentCount
    ) {
        return new DiaryDetailRes(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMoodEmoji(),
                post.getVisibility(),
                post.getImages().stream().map(DiaryImage::getImageUrl).toList(),
                post.getCreatedAt(),
                liked,
                likeCount,
                commentCount
        );
    }
}
package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.entity.DiaryImage;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "다이어리 게시글 작성 응답 DTO")
public record DiaryPostCreateRes(
        String title,
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static DiaryPostCreateRes from(DiaryPost post) {
        return new DiaryPostCreateRes(
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
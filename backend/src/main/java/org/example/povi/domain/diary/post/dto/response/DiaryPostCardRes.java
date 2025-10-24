package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper;
import org.example.povi.domain.diary.post.view.PostViewStats;

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
        boolean liked,
        long likeCount,
        long commentCount

) {
    public static DiaryPostCardRes from(DiaryPost post, PostViewStats stats) {
        return new DiaryPostCardRes(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                DiaryPreviewMapper.buildPreviewText(post.getContent(), 100),
                DiaryPreviewMapper.firstImageUrl(post),
                post.getMoodEmoji(),
                post.getVisibility(),
                post.getCreatedAt().toLocalDate(),
                stats.likedByMe(),
                stats.likeCount(),
                stats.commentCount()
        );
    }
}
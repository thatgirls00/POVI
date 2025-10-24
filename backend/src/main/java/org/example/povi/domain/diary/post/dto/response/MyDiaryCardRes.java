package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryPreviewMapper;
import org.example.povi.domain.diary.post.view.PostViewStats;

import java.time.LocalDate;

@Schema(description = "내 다이어리 게시글 카드 응답 DTO")
public record MyDiaryCardRes(
        Long postId,
        String title,
        String preview,
        MoodEmoji moodEmoji,
        String thumbnailUrl,
        Visibility visibility,
        LocalDate createdDate,
        boolean liked,
        long likeCount,
        long commentCount
) {

    public static MyDiaryCardRes from(DiaryPost post, PostViewStats stats) {
        return new MyDiaryCardRes(
                post.getId(),
                post.getTitle(),
                DiaryPreviewMapper.buildPreviewText(post.getContent(), 100),
                post.getMoodEmoji(),
                DiaryPreviewMapper.firstImageUrl(post),
                post.getVisibility(),
                post.getCreatedAt().toLocalDate(),
                stats.likedByMe(),
                stats.likeCount(),
                stats.commentCount()
        );
    }
}
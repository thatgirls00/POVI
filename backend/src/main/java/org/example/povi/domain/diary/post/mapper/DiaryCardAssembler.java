package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.dto.response.FriendDiaryCardRes;
import org.example.povi.domain.diary.post.dto.response.MyDiaryCardRes;
import org.example.povi.domain.diary.post.entity.DiaryPost;

public final class DiaryCardAssembler {

    private static final int PREVIEW_MAX = 100;

    private DiaryCardAssembler() {}

    /** 나의 다이어리 카드 변환 */
    public static MyDiaryCardRes toMyCard(DiaryPost post) {
        String preview   = DiaryPreviewMapper.buildPreviewText(post.getContent(), PREVIEW_MAX);
        String thumbnail = DiaryPreviewMapper.firstImageUrl(post);

        return new MyDiaryCardRes(
                post.getId(),
                post.getTitle(),
                preview,
                post.getMoodEmoji(),
                thumbnail,
                post.getVisibility(),
                post.getCreatedAt().toLocalDate()
        );
    }

    /** 친구 다이어리 카드 변환 */
    public static FriendDiaryCardRes toFriendCard(DiaryPost post) {
        String preview   = DiaryPreviewMapper.buildPreviewText(post.getContent(), PREVIEW_MAX);
        String thumbnail = DiaryPreviewMapper.firstImageUrl(post);

        return new FriendDiaryCardRes(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                preview,
                thumbnail,
                post.getMoodEmoji(),
                post.getVisibility(),
                post.getCreatedAt().toLocalDate()
        );
    }
}
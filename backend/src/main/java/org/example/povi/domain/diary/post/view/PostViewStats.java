package org.example.povi.domain.diary.post.view;

import java.util.Map;
import java.util.Set;

/**
 * 게시글 단위의 "뷰 통계" 정보를 표현합니다.
 * - 좋아요 여부
 * - 좋아요 수
 * - 댓글 수
 */
public record PostViewStats(boolean likedByMe, long likeCount, long commentCount) {

    public static PostViewStats of(
            Set<Long> likedPostIds,
            Map<Long, Long> likeCountByPostId,
            Map<Long, Long> commentCountByPostId,
            Long postId
    ) {
        return new PostViewStats(
                likedPostIds.contains(postId),
                likeCountByPostId.getOrDefault(postId, 0L),
                commentCountByPostId.getOrDefault(postId, 0L)
        );
    }
}
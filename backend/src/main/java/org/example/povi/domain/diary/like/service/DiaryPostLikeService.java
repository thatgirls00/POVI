package org.example.povi.domain.diary.like.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes;
import org.example.povi.domain.diary.like.entity.DiaryPostLike;
import org.example.povi.domain.diary.like.repository.DiaryPostLikeRepository;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class DiaryPostLikeService {

    private final DiaryPostLikeRepository diaryPostLikeRepository;
    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;
    private final FollowService followService;

    /**
     * 좋아요 토글: 결과 DTO 반환 (liked=true: 추가, false: 취소)
     */
    @Transactional
    public DiaryPostLikeRes toggle(Long postId, Long userId) {
         DiaryPost post = checkAccessOrThrow(postId, userId);
        User user = findUserOrThrow(userId);

        final boolean liked;
        if (diaryPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            diaryPostLikeRepository.deleteByPostIdAndUserId(postId, userId);
            liked = false;
        } else {
            diaryPostLikeRepository.save(DiaryPostLike.of(post, user));
            liked = true;
        }

        long count = countLikesByPostId(postId);
        return new DiaryPostLikeRes(liked, count);
    }

    /**
     * 내 좋아요 여부 + 현재 좋아요 수
     */
    @Transactional(readOnly = true)
    public DiaryPostLikeRes me(Long postId, Long userId) {
        checkAccessOrThrow(postId, userId);
        boolean liked = diaryPostLikeRepository.existsByPostIdAndUserId(postId, userId);
        long count = countLikesByPostId(postId);

        return new DiaryPostLikeRes(liked, count);
    }

    /**
     * 좋아요 수
     */
    @Transactional(readOnly = true)
    public long count(Long postId) {
        if (!diaryPostRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.");
        }
        return countLikesByPostId(postId);
    }


    private long countLikesByPostId(Long postId) {
        return diaryPostLikeRepository.countByPostId(postId);
    }

    /** 존재/가시성 검증 후 게시글 반환 */
    private DiaryPost checkAccessOrThrow(Long postId, Long viewerId) {
        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
        if (!canAccessPost(viewerId, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 게시글에 접근할 수 없습니다.");
        }
        return post;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."));
    }

    /**
     * 댓글 서비스와 동일한 접근 권한 로직
     * - 본인 글: 허용
     * - PUBLIC: 허용
     * - FRIEND: 맞팔만 허용
     * - PRIVATE: 불허
     */
    private boolean canAccessPost(Long viewerId, DiaryPost post) {
        Long ownerId = post.getUser().getId();
        if (viewerId.equals(ownerId)) return true;

        return post.getVisibility().canAccess(viewerId, ownerId, followService);
    }
}


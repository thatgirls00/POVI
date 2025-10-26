package org.example.povi.domain.diary.post.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.like.repository.DiaryPostLikeRepository;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryCardAssembler;
import org.example.povi.domain.diary.post.mapper.DiaryQueryMapper;
import org.example.povi.domain.diary.post.mapper.DiaryRequestMapper;
import org.example.povi.domain.diary.post.mapper.MyDiaryAssembler;
import org.example.povi.domain.diary.post.policy.DiaryPostAccessPolicy;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiaryPostService {

    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;
    private final FollowService followService;
    private final DiaryPostLikeRepository diaryPostLikeRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryPostAccessPolicy postAccessPolicy;

    /**
     * 다이어리 생성 (로그인 필수)
     */
    @Transactional
    public DiaryPostCreateRes createDiaryPost(DiaryPostCreateReq req, Long currentUserId) {
        requireLogin(currentUserId);

        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        try {
            DiaryPost post = DiaryRequestMapper.fromCreateRequest(req, author); // 엔티티가 자체 정제/검증
            DiaryPost saved = diaryPostRepository.save(post);
            return DiaryPostCreateRes.from(saved);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 단일 상세 (로그인 + 접근권한)
     * - 좋아요 여부/수, 댓글 수 포함
     */
    @Transactional(readOnly = true)
    public DiaryDetailRes getDiaryPostDetail(Long postId, Long currentUserId) {
        requireLogin(currentUserId);

        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));

        if (!postAccessPolicy.hasReadPermission(currentUserId, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.");
        }

        boolean liked = diaryPostLikeRepository.existsByPostIdAndUserId(postId, currentUserId);
        long likeCount = diaryPostLikeRepository.countByPostId(postId);
        long commentCount = diaryCommentRepository.countByPostId(postId);

        return DiaryDetailRes.of(post, liked, likeCount, commentCount);
    }

    /**
     * 내 다이어리 목록 + 주간 통계(월~일) (로그인)
     */
    @Transactional(readOnly = true)
    public MyDiaryListRes getMyDiaryPostsWithWeeklyStats(Long currentUserId) {
        requireLogin(currentUserId);

        List<DiaryPost> myPosts = diaryPostRepository.findByUserIdOrderByCreatedAtDesc(currentUserId);
        if (myPosts.isEmpty()) {
            return new MyDiaryListRes(0, 0, new MoodSummaryRes(0.0, MoodEmoji.fromValence(0.0)), List.of());
        }

        List<Long> postIds = myPosts.stream().map(DiaryPost::getId).toList();
        Map<Long, Long> commentCnt = DiaryQueryMapper.toCountMap(diaryCommentRepository.countByPostIds(postIds));
        Map<Long, Long> likeCnt = DiaryQueryMapper.toCountMap(diaryPostLikeRepository.countByPostIds(postIds));
        Set<Long> likedSet = new HashSet<>(diaryPostLikeRepository.findPostIdsLikedByUser(postIds, currentUserId));

        return MyDiaryAssembler.build(myPosts, likedSet, likeCnt, commentCnt, LocalDate.now());
    }

    /**
     * 친구 피드 (로그인)
     * - 맞팔: FRIEND+PUBLIC, 단방향: PUBLIC
     */
    @Transactional(readOnly = true)
    public List<DiaryPostCardRes> listFriendDiaries(Long currentUserId) {
        requireLogin(currentUserId);

        Set<Long> followingIds = followService.getFollowingUserIds(currentUserId);
        if (followingIds.isEmpty()) return List.of();

        Set<Long> mutualIds = followService.getMutualUserIds(currentUserId);
        Set<Long> oneWayIds = new HashSet<>(followingIds);
        oneWayIds.removeAll(mutualIds);

        List<DiaryPost> friendPosts = new ArrayList<>();
        if (!mutualIds.isEmpty()) {
            friendPosts.addAll(diaryPostRepository.findByAuthorsAndVisibilityOrderByCreatedAtDesc(
                    mutualIds, List.of(Visibility.FRIEND, Visibility.PUBLIC)));
        }
        if (!oneWayIds.isEmpty()) {
            friendPosts.addAll(diaryPostRepository.findByAuthorsAndVisibilityOrderByCreatedAtDesc(
                    oneWayIds, List.of(Visibility.PUBLIC)));
        }
        if (friendPosts.isEmpty()) return List.of();

        friendPosts.sort(Comparator.comparing(DiaryPost::getCreatedAt).reversed());

        List<Long> postIds = friendPosts.stream().map(DiaryPost::getId).toList();
        Map<Long, Long> commentCnt = DiaryQueryMapper.toCountMap(diaryPostRepository.countCommentsInPostIds(postIds));
        Map<Long, Long> likeCnt = DiaryQueryMapper.toCountMap(diaryPostLikeRepository.countByPostIds(postIds));
        Set<Long> likedSet = new HashSet<>(diaryPostLikeRepository.findPostIdsLikedByUser(postIds, currentUserId));

        return DiaryCardAssembler.toCards(friendPosts, likedSet, likeCnt, commentCnt);
    }

    /**
     * 모두의 다이어리(Explore) (로그인)
     * - 기간: 최근 7일
     * - 맞팔: FRIEND+PUBLIC, 그 외: PUBLIC
     */
    @Transactional(readOnly = true)
    public List<DiaryPostCardRes> listExploreFeed(Long currentUserId) {
        requireLogin(currentUserId);

        LocalDate today = LocalDate.now();
        LocalDateTime startAt = today.minusDays(6).atStartOfDay(); // 오늘 포함 7일
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();  // 내일 0시(미만)

        Set<Long> mutualIds = followService.getMutualUserIds(currentUserId);

        List<DiaryPost> posts = mutualIds.isEmpty()
                ? diaryPostRepository.findExploreFeedPublicOnlyInPeriod(
                currentUserId, Visibility.PUBLIC, startAt, endAt)
                : diaryPostRepository.findExploreFeedWithMutualsInPeriod(
                currentUserId, mutualIds,
                List.of(Visibility.FRIEND, Visibility.PUBLIC),
                Visibility.PUBLIC, startAt, endAt);

        if (posts.isEmpty()) return List.of();

        List<Long> postIds = posts.stream().map(DiaryPost::getId).toList();
        Map<Long, Long> commentCnt = DiaryQueryMapper.toCountMap(diaryPostRepository.countCommentsInPostIds(postIds));
        Map<Long, Long> likeCnt = DiaryQueryMapper.toCountMap(diaryPostLikeRepository.countByPostIds(postIds));
        Set<Long> likedSet = new HashSet<>(diaryPostLikeRepository.findPostIdsLikedByUser(postIds, currentUserId));

        return DiaryCardAssembler.toCards(posts, likedSet, likeCnt, commentCnt);
    }


    /**
     * 다이어리 부분 수정 (로그인 + 소유자)
     * - null 필드는 미변경
     */
    @Transactional
    public DiaryPostUpdateRes updateDiaryPost(Long postId, DiaryPostUpdateReq req, Long currentUserId) {
        requireLogin(currentUserId);

        DiaryPost post = getOwnedDiaryPostOrThrow(postId, currentUserId);

        try {
            if (req.title() != null) post.renameTo(req.title());
            if (req.content() != null) post.rewriteContent(req.content());
            if (req.moodEmoji() != null) post.changeMood(req.moodEmoji());
            if (req.visibility() != null) post.changeVisibility(req.visibility());
            if (req.imageUrls() != null) post.replaceImages(req.imageUrls());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return DiaryPostUpdateRes.from(post);
    }

    /**
     * 다이어리 삭제 (로그인 + 소유자)
     */
    @Transactional
    public void deleteDiaryPost(Long postId, Long currentUserId) {
        requireLogin(currentUserId);
        DiaryPost post = getOwnedDiaryPostOrThrow(postId, currentUserId);
        diaryPostRepository.delete(post);
    }

    // =========================================================
    // Private Helpers
    // =========================================================

    /**
     * 로그인 필수 동작에서 userId null 방지
     */
    private void requireLogin(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    /**
     * 소유자 검증을 포함한 단건 조회
     */
    private DiaryPost getOwnedDiaryPostOrThrow(Long postId, Long currentUserId) {
        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 접근할 수 있습니다.");
        }
        return post;
    }

    /**
     * 사용자별 다이어리 개수
     */
    @Transactional(readOnly = true)
    public long getDiaryPostCountForUser(Long userId) {
        return diaryPostRepository.countByUserId(userId);
    }
}
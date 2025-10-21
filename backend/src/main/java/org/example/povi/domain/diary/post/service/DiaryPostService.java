package org.example.povi.domain.diary.post.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryCardAssembler;
import org.example.povi.domain.diary.post.mapper.DiaryRequestMapper;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiaryPostService {

    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;
    private final FollowService followService;

    /** 다이어리 생성 */
    @Transactional
    public DiaryPostCreateRes createDiaryPost(DiaryPostCreateReq createReq, Long currentUserId) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        DiaryPost diaryPost = DiaryRequestMapper.fromCreateRequest(createReq, author);

        if (createReq.imageUrls() != null) {
            List<String> normalizedImageUrls = sanitizeImageUrls(createReq.imageUrls());
            diaryPost.replaceImages(normalizedImageUrls);
        }

        diaryPostRepository.save(diaryPost);
        return DiaryPostCreateRes.from(diaryPost);
    }

    /** 다이어리 부분 수정 (제목/내용/이모지/공개범위/이미지 선택적 업데이트) */
    @Transactional
    public DiaryPostUpdateRes updateDiaryPost(Long diaryPostId, DiaryPostUpdateReq updateReq, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);

        // 제목: null=미변경, 공백만 금지
        if (updateReq.title() != null) {
            String newTitle = updateReq.title().trim();
            if (newTitle.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 공백만으로 수정할 수 없습니다.");
            }
            diaryPost.renameTo(newTitle);
        }

        // 내용: null=미변경, 공백만 금지
        if (updateReq.content() != null) {
            String newContent = updateReq.content().trim();
            if (newContent.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 공백만으로 수정할 수 없습니다.");
            }
            diaryPost.rewriteContent(newContent);
        }

        // 이모지/공개범위: 값이 오면 변경
        if (updateReq.moodEmoji() != null) diaryPost.changeMood(updateReq.moodEmoji());
        if (updateReq.visibility() != null) diaryPost.changeVisibility(updateReq.visibility());

        // 이미지: null=미변경 / []=전체삭제 / 값있음=전체교체
        if (updateReq.imageUrls() != null) {
            List<String> sanitizedImageUrls = sanitizeImageUrls(updateReq.imageUrls());
            diaryPost.replaceImages(sanitizedImageUrls);
        }

        return DiaryPostUpdateRes.from(diaryPost);
    }

    /** 다이어리 삭제 (소유자만) */
    @Transactional
    public void deleteDiaryPost(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);
        diaryPostRepository.delete(diaryPost);
    }

    /** 단일 상세 조회 (소유자 전용) */
    //현재 정책: 소유자만 상세 조회 허용 (친구/공개 상세는 처리 예정)
    @Transactional(readOnly = true)
    public DiaryDetailRes getMyDiaryPostDetail(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);
        return DiaryDetailRes.from(diaryPost);
    }

    /** 내 다이어리 목록 + 이번 주 통계(월~일) */
    @Transactional(readOnly = true)
    public MyDiaryListRes getMyDiaryPostsWithWeeklyStats(Long currentUserId) {
        // 최신순 목록
        List<DiaryPost> myPostsNewestFirst = diaryPostRepository.findByUserIdOrderByCreatedAtDesc(currentUserId);

        List<MyDiaryCardRes> myDiaryCards = myPostsNewestFirst.stream()
                .map(DiaryCardAssembler::toMyCard)
                .toList();

        // 총 개수
        long totalPostCount = myPostsNewestFirst.size();

        // 이번 주 글만 필터
        LocalDate today = LocalDate.now();
        LocalDate weekStartMonday = today.with(DayOfWeek.MONDAY);
        LocalDate weekEndSunday = today.with(DayOfWeek.SUNDAY);

        //이번 주 다이어리 필터링
        List<DiaryPost> postsThisWeek = myPostsNewestFirst.stream()
                .filter(d -> {
                    LocalDate createdDate = d.getCreatedAt().toLocalDate();
                    return !createdDate.isBefore(weekStartMonday) && !createdDate.isAfter(weekEndSunday);
                })
                .toList();

        // 이번 주 개수/평균 감정/대표 이모지
        long weeklyPostCount = postsThisWeek.size();
        double weeklyAverageValence = postsThisWeek.stream()
                .mapToInt(p -> p.getMoodEmoji().valence())
                .average()
                .orElse(0.0);
        MoodEmoji weeklyRepresentativeEmoji = MoodEmoji.fromValence(weeklyAverageValence);

        return new MyDiaryListRes(
                totalPostCount,
                weeklyPostCount,
                new MoodSummaryRes(weeklyAverageValence, weeklyRepresentativeEmoji),
                myDiaryCards
        );
    }

    /** 친구 피드: 맞팔=FRIEND+PUBLIC, 단방향=PUBLIC (최신순) */
    @Transactional(readOnly = true)
    public List<DiaryPostCardRes> listFriendDiaries(Long currentUserId) {

        // 내가 팔로우 중인 사용자 (없으면 빈 목록)
        Set<Long> followingUserIds = followService.getFollowingIds(currentUserId);
        if (followingUserIds.isEmpty()) return List.of();

        // 맞팔 사용자 / 단방향 사용자 분리
        Set<Long> mutualFriendIds = followService.getMutualFriendIds(currentUserId);
        Set<Long> oneWayFollowingIds = new HashSet<>(followingUserIds);
        oneWayFollowingIds.removeAll(mutualFriendIds);

        // 가시성에 따라 조회 후 합치기
        List<DiaryPost> friendPosts = new ArrayList<>();
        if (!mutualFriendIds.isEmpty()) {
            friendPosts.addAll(
                    diaryPostRepository.findByAuthorsAndVisibilityOrderByCreatedAtDesc(
                            mutualFriendIds, List.of(Visibility.FRIEND, Visibility.PUBLIC)
                    )
            );
        }
        if (!oneWayFollowingIds.isEmpty()) {
            friendPosts.addAll(
                    diaryPostRepository.findByAuthorsAndVisibilityOrderByCreatedAtDesc(
                            oneWayFollowingIds, List.of(Visibility.PUBLIC)
                    )
            );
        }

        // 최신순 정렬 → 카드 변환
        return friendPosts.stream()
                .sorted(Comparator.comparing(DiaryPost::getCreatedAt).reversed())
                .map(DiaryCardAssembler::toDiaryCard)
                .toList();
    }

    /** 모두의 다이어리(Explore): 맞팔=FRIEND+PUBLIC, 그 외=PUBLIC (최근 7일, 최신순) */
    @Transactional(readOnly = true)
    public List<DiaryPostCardRes> listExploreFeed(Long currentUserId) {

        // 최근 7일 범위 (오늘 포함, [startAt, endAt))
        LocalDate today = LocalDate.now();
        LocalDateTime startAt = today.minusDays(6).atStartOfDay();
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();

        // 맞팔 사용자 집합
        Set<Long> mutualIds = followService.getMutualFriendIds(currentUserId);

        // 맞팔 여부에 따라 조회 분기 (내 글 제외는 쿼리에서 처리)
        List<DiaryPost> posts = mutualIds.isEmpty()
                ? diaryPostRepository.findExploreFeedPublicOnlyInPeriod(
                currentUserId,
                Visibility.PUBLIC,
                startAt,
                endAt
        )
                : diaryPostRepository.findExploreFeedWithMutualsInPeriod(
                currentUserId,
                mutualIds,
                List.of(Visibility.FRIEND, Visibility.PUBLIC),
                Visibility.PUBLIC,
                startAt,
                endAt
        );

        return posts.stream()
                .map(DiaryCardAssembler::toDiaryCard)
                .toList();
    }

    // =========================================================
    // Private Helpers
    // =========================================================

    /** 이미지 URL 정리: trim → 빈값 제거 → 중복 제거 */
    private List<String> sanitizeImageUrls(List<String> urls) {
        if (urls == null) return null;
        return urls.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    /** 소유자 검증 포함한 단건 조회 */
    private DiaryPost getOwnedDiaryPostOrThrow(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = diaryPostRepository.findById(diaryPostId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));
        if (!diaryPost.getUser().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 접근할 수 있습니다.");
        }

        return diaryPost;
    }
}
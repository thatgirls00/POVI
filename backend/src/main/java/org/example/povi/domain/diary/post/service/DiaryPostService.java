package org.example.povi.domain.diary.post.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryCardAssembler;
import org.example.povi.domain.diary.post.mapper.DiaryRequestMapper;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiaryPostService {

    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;
    private final FollowService followService;

    /**
     * 다이어리 생성
     */
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

    /**
     * 다이어리 부분 수정
     */
    @Transactional
    public DiaryPostUpdateRes updateDiaryPost(Long diaryPostId, DiaryPostUpdateReq updateReq, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);

        // 제목 (null=미변경, 값 있으면 trim 후 공백만 금지)
        if (updateReq.title() != null) {
            String newTitle = updateReq.title().trim();
            if (newTitle.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 공백만으로 수정할 수 없습니다.");
            }
            diaryPost.renameTo(newTitle);
        }

        // 내용
        if (updateReq.content() != null) {
            String newContent = updateReq.content().trim();
            if (newContent.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 공백만으로 수정할 수 없습니다.");
            }
            diaryPost.rewriteContent(newContent);
        }

        if (updateReq.moodEmoji() != null) diaryPost.changeMood(updateReq.moodEmoji());
        if (updateReq.visibility() != null) diaryPost.changeVisibility(updateReq.visibility());

        // 이미지: null=미변경 / []=전체삭제 / 값있음=전체교체
        if (updateReq.imageUrls() != null) {
            List<String> sanitizedImageUrls = sanitizeImageUrls(updateReq.imageUrls());
            diaryPost.replaceImages(sanitizedImageUrls);
        }
        return DiaryPostUpdateRes.from(diaryPost);
    }

    //다이어리 삭제
    @Transactional
    public void deleteDiaryPost(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);
        diaryPostRepository.delete(diaryPost);
    }

    //다이어리 조회 - 상세조회
    //현재 정책: 소유자만 상세 조회 허용 (친구/공개 상세는 처리 예정)
    @Transactional(readOnly = true)
    public DiaryDetailRes getMyDiaryPostDetail(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = getOwnedDiaryPostOrThrow(diaryPostId, currentUserId);
        return DiaryDetailRes.from(diaryPost);
    }

    //나의 다이어리 목록 + 통계 조회
    @Transactional(readOnly = true)
    public MyDiaryListRes getMyDiaryPostsWithWeeklyStats(Long currentUserId) {
        List<DiaryPost> myPostsNewestFirst = diaryPostRepository.findByUserIdOrderByCreatedAtDesc(currentUserId);

        List<MyDiaryCardRes> myDiaryCards = myPostsNewestFirst.stream()
                .map(DiaryCardAssembler::toMyCard)
                .toList();

        //통계 - 총 작성 개수
        long totalPostCount = myPostsNewestFirst.size();

        //이번 주 기간 계산(월~일)
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

        //통계 - 이번 주 작성 개수
        long weeklyPostCount = postsThisWeek.size();

        //통계 - 이번주 평균 감정 계산
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

    @Transactional(readOnly = true)
    public List<FriendDiaryCardRes> listFriendDiaries(Long currentUserId) {

        Set<Long> followingUserIds = followService.getFollowingIds(currentUserId);
        if (followingUserIds.isEmpty()) return List.of();

        Set<Long> mutualFriendIds = followService.getMutualFriendIds(currentUserId);

        Set<Long> oneWayFollowingIds = new HashSet<>(followingUserIds);
        oneWayFollowingIds.removeAll(mutualFriendIds);

        //가시성 별로 조회 (전체 기간, 최신순)
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

        //최신순으로 최종 정렬(두 리스트 합쳤으니 한 번 정렬) + 카드 변환
        return friendPosts.stream()
                .sorted(Comparator.comparing(DiaryPost::getCreatedAt).reversed())
                .map(DiaryCardAssembler::toFriendCard)
                .toList();
    }


    // 공통 이미지 정리: null은 null 유지, 값 있으면 trim + distinct
    private List<String> sanitizeImageUrls(List<String> urls) {
        if (urls == null) return null;
        return urls.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    private DiaryPost getOwnedDiaryPostOrThrow(Long diaryPostId, Long currentUserId) {
        DiaryPost diaryPost = diaryPostRepository.findById(diaryPostId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));
        if (!diaryPost.getUser().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 접근할 수 있습니다.");
        }

        return diaryPost;
    }
}
package org.example.povi.domain.diary.post.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.mapper.DiaryCardAssembler;
import org.example.povi.domain.diary.post.mapper.DiaryRequestMapper;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryPostService {

    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryPostCreateRes createPost(DiaryPostCreateReq request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        DiaryPost post = DiaryRequestMapper.fromCreateRequest(request, author);

        if (request.imageUrls() != null) {
            List<String> normalizedImageUrls = normalizeImageUrls(request.imageUrls());
            post.replaceImages(normalizedImageUrls);
        }

        diaryPostRepository.save(post);
        return DiaryPostCreateRes.from(post);
    }

    //다이어리 수정
    @Transactional
    public DiaryPostUpdateRes patchPost(Long postId, DiaryPostUpdateReq request, Long requesterId) {
        DiaryPost post = loadOwnedPost(postId, requesterId);

        // 제목 (null=미변경, 값 있으면 trim 후 공백만 금지)
        if (request.title() != null) {
            String newTitle = request.title().trim();
            if (newTitle.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 공백만으로 수정할 수 없습니다.");
            }
            post.renameTo(newTitle);
        }

        // 내용
        if (request.content() != null) {
            String newContent = request.content().trim();
            if (newContent.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 공백만으로 수정할 수 없습니다.");
            }
            post.rewriteContent(newContent);
        }

        if (request.moodEmoji() != null) post.changeMood(request.moodEmoji());
        if (request.visibility() != null) post.changeVisibility(request.visibility());

        // 이미지: null=미변경 / []=전체삭제 / 값있음=전체교체
        if (request.imageUrls() != null) {
            List<String> normalized = normalizeImageUrls(request.imageUrls());
            post.replaceImages(normalized);
        }
        return DiaryPostUpdateRes.from(post);
    }

    //다이어리 삭제
    @Transactional
    public void deletePost(Long postId, Long requesterId) {
        DiaryPost post = loadOwnedPost(postId, requesterId);
        diaryPostRepository.delete(post);
    }

    //다이어리 조회 - 상세조회
    @Transactional(readOnly = true)
    public DiaryDetailRes getPostDetail(Long postId, Long requesterId) {
        DiaryPost post = loadOwnedPost(postId, requesterId);
        return DiaryDetailRes.from(post);
    }

    //나의 다이어리 목록 + 통계 조회
    @Transactional(readOnly = true)
    public MyDiaryListRes getMyPosts(Long authorId) {
        List<DiaryPost> posts = diaryPostRepository.findByUserIdOrderByCreatedAtDesc(authorId);

        List<MyDiaryListItemRes> cards = posts.stream()
                .map(DiaryCardAssembler::toCard)
                .toList();

        //통계 - 총 작성 개수
        long totalCount = posts.size();

        //이번 주 기간 계산(월~일)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        //이번 주 다이어리 필터링
        List<DiaryPost> thisWeeksPosts = posts.stream()
                .filter(d -> {
                    LocalDate created = d.getCreatedAt().toLocalDate();
                    return !created.isBefore(weekStart) && !created.isAfter(weekEnd);
                })
                .toList();

        //통계 - 이번 주 작성 개수
        long thisWeekCount = thisWeeksPosts.size();

        //통계 - 이번주 평균 감정 계산
        double averageValence = thisWeeksPosts.stream()
                .mapToInt(d -> d.getMoodEmoji().valence())
                .average()
                .orElse(0.0);

        MoodEmoji representativeEmoji = MoodEmoji.fromValence(averageValence);

        return new MyDiaryListRes(
                totalCount,
                thisWeekCount,
                new MoodSummaryRes(averageValence, representativeEmoji),
                cards
        );
    }


    // 공통 이미지 정리: null은 null 유지, 값 있으면 trim + distinct
    private List<String> normalizeImageUrls(List<String> urls) {
        if (urls == null) return null;
        return urls.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    private DiaryPost loadOwnedPost(Long diaryId, Long userId) {
        DiaryPost post = diaryPostRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));
        if (!post.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 접근할 수 있습니다.");
        }
        return post;
    }
}
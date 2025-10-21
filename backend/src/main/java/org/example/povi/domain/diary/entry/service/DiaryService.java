package org.example.povi.domain.diary.entry.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.entry.dto.request.DiaryCreateReq;
import org.example.povi.domain.diary.entry.dto.request.DiaryUpdateReq;
import org.example.povi.domain.diary.entry.dto.response.*;
import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.example.povi.domain.diary.entry.entity.DiaryImage;
import org.example.povi.domain.diary.entry.repository.DiaryRepository;
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
public class DiaryService {

    private static final int TITLE_MIN = 2;
    private static final int TITLE_MAX = 50;
    private static final int CONTENT_MIN = 10;
    private static final int CONTENT_MAX = 3000;
    private static final int IMG_MAX = 3;
    private static final int URL_MAX = 2048;

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryCreateRes create(DiaryCreateReq req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        DiaryEntry diaryEntry = DiaryEntry.builder()
                .user(user)
                .title(req.title())
                .content(req.content())
                .moodEmoji(req.moodEmoji())
                .visibility(req.visibility())
                .build();

        List<String> urls = normalizeImages(req.imageUrls());
        if (urls != null) {
            urls.forEach(u -> diaryEntry.addImage(new DiaryImage(diaryEntry, u)));
        }

        diaryRepository.save(diaryEntry);
        return DiaryCreateRes.from(diaryEntry);
    }

    //다이어리 수정
    @Transactional
    public DiaryUpdateRes update(Long diaryId, DiaryUpdateReq req, Long userId) {
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary not found"));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 수정할 수 있습니다.");
        }

        // 제목 (null=미변경, 값 있으면 trim 후 공백만 금지)
        if (req.title() != null) {
            String t = req.title().trim();
            if (t.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 공백만으로 수정할 수 없습니다.");
            diaryEntry.renameTo(t);
        }

        // 내용
        if (req.content() != null) {
            String c = req.content().trim();
            if (c.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 공백만으로 수정할 수 없습니다.");
            diaryEntry.rewriteContent(c);
        }

        if (req.moodEmoji() != null) diaryEntry.changeMood(req.moodEmoji());
        if (req.visibility() != null) diaryEntry.changeVisibility(req.visibility());

        // 이미지: null=미변경 / []=전체삭제 / 값있음=전체교체
        if (req.imageUrls() != null) {
            List<String> normalized = normalizeImages(req.imageUrls());
            diaryEntry.replaceImages(normalized);
        }

        return DiaryUpdateRes.from(diaryEntry);
    }


    // 공통 이미지 정리: null은 null 유지, 값 있으면 trim + distinct
    private List<String> normalizeImages(List<String> urls) {
        if (urls == null) return null;
        return urls.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    //다이어리 삭제
    @Transactional
    public void delete(Long diaryId, Long userId) {

        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 삭제할 수 있습니다.");
        }
        diaryRepository.delete(diary);
    }

    //다이어리 조회 - 상세조회
    @Transactional(readOnly = true)
    public DiaryDetailRes getDiaryDetail(Long diaryId, Long userId) {
        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 다이어리입니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 다이어리만 조회할 수 있습니다.");
        }

        return DiaryDetailRes.from(diary);
    }

    //나의 다이어리 목록 + 통계 조회
    @Transactional(readOnly = true)
    public MyDiaryListRes getMyDiaries(Long userId) {
        List<DiaryEntry> diaries = diaryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<MyDiaryListItemRes> cards = diaries.stream()
                .map(d -> new MyDiaryListItemRes(
                        d.getId(),
                        d.getTitle(),
                        buildPreviewText(d.getContent(), 60),
                        d.getMoodEmoji(),
                        firstImageUrl(d),
                        d.getVisibility(),
                        d.getCreatedAt().toLocalDate()
                ))
                .toList();

        //통계 - 총 작성 개수
        long totalCount = diaries.size();

        //이번 주(월~일)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        //이번 주 다이어리 필터
        List<DiaryEntry> thisWeeks = diaries.stream()
                .filter(d -> {
                    LocalDate created = d.getCreatedAt().toLocalDate();
                    return !created.isBefore(weekStart) && !created.isAfter(weekEnd);
                })
                .toList();

        //통계 - 이번 주 작성 개수
        long thisWeekCount = thisWeeks.size();

        //통계 - 이번주 평균 감정 점수
        double averageScore = thisWeeks.stream()
                .mapToInt(d -> d.getMoodEmoji().valence())
                .average()
                .orElse(0.0);

        MoodEmoji representative = MoodEmoji.fromValence(averageScore);

        return new MyDiaryListRes(
                totalCount,
                thisWeekCount,
                new MoodSummaryRes(averageScore, representative),
                cards
        );
    }

    private static String firstImageUrl(DiaryEntry d) {
        return d.getImages().isEmpty() ? null : d.getImages().get(0).getImageUrl();
    }

    private static String buildPreviewText(String content, int maxLength) {
        if (content == null || content.isBlank()) return "";

        String compact = content.replaceAll("\\s+", " ").trim();

        if (compact.length() <= maxLength) return compact;

        return compact.substring(0, maxLength) + "...";
    }
}
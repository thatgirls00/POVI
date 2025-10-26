package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.post.dto.response.MoodSummaryRes;
import org.example.povi.domain.diary.post.dto.response.MyDiaryCardRes;
import org.example.povi.domain.diary.post.dto.response.MyDiaryListRes;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.view.PostViewStats;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MyDiaryAssembler {

    private MyDiaryAssembler() {}

    public static MyDiaryListRes build(
            List<DiaryPost> myPostsNewestFirst,
            Set<Long> likedSet,
            Map<Long, Long> likeCnt,
            Map<Long, Long> cmtCnt,
            LocalDate today
    ) {
        // 카드 리스트 조립
        List<MyDiaryCardRes> cards = myPostsNewestFirst.stream()
                .map(p -> MyDiaryCardRes.from(
                        p,
                        PostViewStats.of(likedSet, likeCnt, cmtCnt, p.getId())
                ))
                .toList();

        long total = myPostsNewestFirst.size();

        // 이번 주 범위
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd   = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 이번 주 글만 필터
        List<DiaryPost> thisWeek = myPostsNewestFirst.stream()
                .filter(p -> {
                    LocalDate d = p.getCreatedAt().toLocalDate();
                    return !d.isBefore(weekStart) && !d.isAfter(weekEnd);
                })
                .toList();

        // 통계 계산
        long weeklyCount = thisWeek.size();
        double avgValence = thisWeek.stream()
                .mapToInt(p -> p.getMoodEmoji().valence())
                .average().orElse(0.0);
        MoodEmoji repEmoji = MoodEmoji.fromValence(avgValence);

        // 최종 DTO
        return new MyDiaryListRes(
                total,
                weeklyCount,
                new MoodSummaryRes(avgValence, repEmoji),
                cards
        );
    }
}

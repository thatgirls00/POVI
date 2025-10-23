package org.example.povi.domain.diary.post.dto.response;

import java.util.List;

public record MyDiaryListRes(
        long totalCount,
        long thisWeekCount,
        MoodSummaryRes moodSummary,
        List<MyDiaryCardRes> myDiaries

) {
    public MyDiaryListRes {
        myDiaries = (myDiaries == null) ? List.of() : List.copyOf(myDiaries);
    }
}

package org.example.povi.domain.diary.entry.dto.response;

import java.util.List;

public record MyDiaryListRes(
        long totalCount,
        long thisWeekCount,
        MoodSummaryRes moodSummary,
        List<MyDiaryListItemRes> myDiaries

) {
    public MyDiaryListRes {
        myDiaries = (myDiaries == null) ? List.of() : List.copyOf(myDiaries);
    }
}

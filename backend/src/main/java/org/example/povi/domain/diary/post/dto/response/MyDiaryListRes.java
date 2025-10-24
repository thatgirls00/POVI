package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "내 다이어리 게시글 목록 응답 DTO")
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

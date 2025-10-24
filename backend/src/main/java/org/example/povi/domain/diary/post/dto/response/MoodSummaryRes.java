package org.example.povi.domain.diary.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.diary.enums.MoodEmoji;

@Schema(description = "기분 요약 응답 DTO")
public record MoodSummaryRes(
        double averageScore,
        MoodEmoji representative
) { }

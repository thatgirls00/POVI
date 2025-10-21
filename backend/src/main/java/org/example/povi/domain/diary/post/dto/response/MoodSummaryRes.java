package org.example.povi.domain.diary.post.dto.response;

import org.example.povi.domain.diary.type.MoodEmoji;

public record MoodSummaryRes(
        double averageScore,
        MoodEmoji representative
) { }

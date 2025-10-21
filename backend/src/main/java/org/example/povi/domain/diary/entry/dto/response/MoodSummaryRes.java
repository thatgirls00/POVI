package org.example.povi.domain.diary.entry.dto.response;

import org.example.povi.domain.diary.type.MoodEmoji;

public record MoodSummaryRes(
        double averageScore,
        MoodEmoji representative
) { }

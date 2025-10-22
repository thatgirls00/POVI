package org.example.povi.domain.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.povi.domain.mission.entity.Mission;

public record CreateTodayMissionsRequest(
        @NotNull
        Mission.EmotionType emotionType,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude
) {}
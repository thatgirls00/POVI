package org.example.povi.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.povi.domain.mission.entity.Mission;

@Schema(description = "오늘 미션 생성을 위한 요청 DTO")
public record CreateTodayMissionsRequest(

        @NotNull
        @Schema(description = "사용자의 감정 상태", example = "HAPPY")
        Mission.EmotionType emotionType,

        @NotNull
        @Schema(description = "위도", example = "37.5665")
        Double latitude,

        @NotNull
        @Schema(description = "경도", example = "126.9780")
        Double longitude

) {}
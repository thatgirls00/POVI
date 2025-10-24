package org.example.povi.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.mission.entity.UserMission;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "사용자의 하루 미션 기록 응답 DTO")
public record MissionHistoryResponse(

        @Schema(description = "미션 날짜", example = "2025-10-24")
        LocalDate missionDate,

        @Schema(description = "미션 목록")
        List<MissionResponse> missions,

        @Schema(description = "완료된 미션 개수", example = "2")
        int completedCount,

        @Schema(description = "전체 미션 개수", example = "5")
        int totalCount,

        @Schema(description = "완료율 (%)", example = "40.0")
        double completionRate

) {
    public MissionHistoryResponse(LocalDate missionDate, List<MissionResponse> missions) {
        this(
                missionDate,
                missions,
                calculateCompletedCount(missions),
                missions.size(),
                calculateCompletionRate(missions)
        );
    }
    // 완료 미션 개수
    private static int calculateCompletedCount(List<MissionResponse> missions) {
        return (int) missions.stream()
                .filter(mission -> mission.status() == UserMission.MissionStatus.COMPLETED)
                .count();
    }
    // 완료율
    private static double calculateCompletionRate(List<MissionResponse> missions) {
        int totalCount = missions.size();
        if (totalCount == 0) return 0;
        int completedCount = calculateCompletedCount(missions);
        return Math.round((double) completedCount / totalCount * 100);
    }
}
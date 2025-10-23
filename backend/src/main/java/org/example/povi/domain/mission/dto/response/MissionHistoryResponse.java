package org.example.povi.domain.mission.dto.response;

import org.example.povi.domain.mission.entity.UserMission;

import java.time.LocalDate;
import java.util.List;

public record MissionHistoryResponse(
        LocalDate missionDate,
        List<MissionResponse> missions,
        int completedCount,
        int totalCount,
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

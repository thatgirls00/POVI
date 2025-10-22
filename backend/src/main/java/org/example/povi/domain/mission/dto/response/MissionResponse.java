package org.example.povi.domain.mission.dto.response;

import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;

public record MissionResponse(
        Long missionId,
        String title,
        String description,
        UserMission.MissionStatus status
) {
    public MissionResponse(Mission mission, UserMission.MissionStatus status) {
        this(
                mission.getId(),
                mission.getTitle(),
                mission.getDescription(),
                status
        );
    }
}

package org.example.povi.domain.mission.dto.response;

import lombok.Getter;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;

@Getter
public class MissionResponse {
    private Long missionId;
    private String title;
    private String description;
    private UserMission.MissionStatus status;

    public MissionResponse(Mission mission, UserMission.MissionStatus status) {
        this.missionId = mission.getId();
        this.title = mission.getTitle();
        this.description = mission.getDescription();
        this.status = status;
    }
}

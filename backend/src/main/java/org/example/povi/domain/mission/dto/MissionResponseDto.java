package org.example.povi.domain.mission.dto;

import lombok.Getter;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;

@Getter
public class MissionResponseDto {
    private Long missionId;
    private String title;
    private String description;
    private String emotionType;
    private UserMission.MissionStatus status;

    public MissionResponseDto(Mission mission, UserMission.MissionStatus status) {
        this.missionId = mission.getId();
        this.title = mission.getTitle();
        this.description = mission.getDescription();
        this.emotionType = mission.getEmotionType().name();
        this.status = status;
    }
}

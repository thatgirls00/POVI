package org.example.povi.domain.mission.dto.response;

import lombok.Getter;
import org.example.povi.domain.mission.entity.UserMission;

import java.time.LocalDate;

@Getter
public class UserMissionResponse {
    private Long userMissionId;
    private Long missionId;
    private String title;
    private String description;
    private String emotionType;
    private String status;
    private LocalDate missionDate;

    public UserMissionResponse(UserMission userMission) {
        this.userMissionId = userMission.getId();
        this.missionId = userMission.getMission().getId();
        this.title = userMission.getMission().getTitle();
        this.description = userMission.getMission().getDescription();
        this.emotionType = userMission.getMission().getEmotionType().name();
        this.status = userMission.getStatus().name();
        this.missionDate = userMission.getMissionDate();
    }
}

package org.example.povi.domain.mission.dto;

import lombok.Getter;
import org.example.povi.domain.mission.entity.UserMission;

import java.time.LocalDateTime;

@Getter
public class UserMissionResponseDto {
    private Long userMissionId;
    private Long missionId;
    private String title;
    private String description;
    private String emotionType;
    private String status;
    private LocalDateTime assignedDate;
    private LocalDateTime completedAt;

    public UserMissionResponseDto(UserMission userMission) {
        this.userMissionId = userMission.getId();
        this.missionId = userMission.getMission().getId();
        this.title = userMission.getMission().getTitle();
        this.description = userMission.getMission().getDescription();
        this.emotionType = userMission.getMission().getEmotionType().name();
        this.status = userMission.getStatus().name();
        this.assignedDate = userMission.getCreatedAt();
        this.completedAt = userMission.getCompletedAt();
    }
}

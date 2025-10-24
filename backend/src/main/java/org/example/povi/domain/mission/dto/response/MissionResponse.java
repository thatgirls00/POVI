package org.example.povi.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;

@Schema(description = "미션 응답 DTO")
public record MissionResponse(

        @Schema(description = "미션 ID", example = "1")
        Long missionId,

        @Schema(description = "미션 제목", example = "밖에서 산책하기")
        String title,

        @Schema(description = "미션 설명", example = "햇빛을 쬐며 산책하면 기분이 좋아져요.")
        String description,

        @Schema(description = "미션 상태", example = "IN_PROGRESS")
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
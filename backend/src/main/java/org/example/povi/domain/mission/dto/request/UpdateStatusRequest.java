package org.example.povi.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.povi.domain.mission.entity.UserMission;

@Schema(description = "유저 미션 상태 변경 요청 DTO")
public record UpdateStatusRequest(

        @NotNull
        @Schema(
                description = "미션 상태",
                example = "COMPLETE",
                implementation = UserMission.MissionStatus.class
        )
        UserMission.MissionStatus status

) {}
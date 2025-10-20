package org.example.povi.domain.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.povi.domain.mission.entity.UserMission;

public record UpdateStatusRequest(
        @NotNull UserMission.MissionStatus status
) {}

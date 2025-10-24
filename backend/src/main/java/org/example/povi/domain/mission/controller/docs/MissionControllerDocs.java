package org.example.povi.domain.mission.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest;
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest;
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse;
import org.example.povi.domain.mission.dto.response.MissionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "미션 API", description = "미션 생성, 조회, 상태 변경, 이력 조회 기능 제공")
public interface MissionControllerDocs {

    @Operation(summary = "오늘의 미션 조회", description = "로그인한 사용자의 오늘 미션 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 조회 성공",
                    content = @Content(schema = @Schema(implementation = MissionResponse.class))),
            @ApiResponse(responseCode = "204", description = "조회된 미션 없음")
    })
    @GetMapping("/today")
    ResponseEntity<List<MissionResponse>> getTodayMissions(
            @Parameter(description = "JWT 인증 토큰")
            @RequestHeader("Authorization") String bearerToken);


    @Operation(summary = "오늘의 미션 생성", description = "현재 사용자의 감정 기반으로 오늘 미션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "미션 생성 성공",
                    content = @Content(schema = @Schema(implementation = MissionResponse.class)))
    })
    @PostMapping("/today")
    ResponseEntity<List<MissionResponse>> createTodayMissions(
            @Parameter(description = "JWT 인증 토큰")
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody CreateTodayMissionsRequest req);


    @Operation(summary = "유저 미션 상태 변경", description = "특정 유저 미션의 완료 여부 상태를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상태 변경 성공")
    })
    @PatchMapping("/{userMissionId}/status")
    ResponseEntity<Void> updateStatus(
            @Parameter(description = "JWT 인증 토큰")
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "유저 미션 ID")
            @PathVariable Long userMissionId,
            @RequestBody UpdateStatusRequest req);


    @Operation(summary = "미션 이력 조회", description = "사용자의 모든 미션 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = MissionHistoryResponse.class))),
            @ApiResponse(responseCode = "204", description = "조회된 이력 없음")
    })
    @GetMapping("/history")
    ResponseEntity<List<MissionHistoryResponse>> getMissionHistory(
            @Parameter(description = "JWT 인증 토큰")
            @RequestHeader("Authorization") String bearerToken);
}
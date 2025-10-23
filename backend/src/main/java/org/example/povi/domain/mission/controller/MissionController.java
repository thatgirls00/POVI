package org.example.povi.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest;
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest;
import org.example.povi.domain.mission.dto.response.MissionResponse;
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse;
import org.example.povi.domain.mission.service.MissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtTokenProvider jwtTokenProvider;

    // resolveToken 함수 선언
    private String resolveToken(String bearerToken) {
        return bearerToken.replace("Bearer ", "");
    }


    // 오늘 미션 조회
    @GetMapping("/today")
    public ResponseEntity<List<MissionResponse>> getTodayMissions(
            @RequestHeader("Authorization") String bearerToken) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        List<MissionResponse> list = missionService.readTodayMissions(userId);
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }


    // 오늘 미션 생성
    @PostMapping("/today")
    public ResponseEntity<List<MissionResponse>> createTodayMissions(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody CreateTodayMissionsRequest req) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        List<MissionResponse> list = missionService.createTodayMissions(userId, req.emotionType(), req.latitude(), req.longitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }


    // 유저 미션 상태 업데이트
    @PatchMapping("/{userMissionId}/status")
    public ResponseEntity<Void> updateStatus(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long userMissionId, 
            @RequestBody @Valid UpdateStatusRequest req) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        missionService.updateUserMissionStatus(userId, userMissionId, req.status());
        return ResponseEntity.noContent().build();
    }


    // 미션 이력 조회
    @GetMapping("/history")
    public ResponseEntity<List<MissionHistoryResponse>> getMissionHistory(
            @RequestHeader("Authorization") String bearerToken) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        List<MissionHistoryResponse> history = missionService.getMissionHistory(userId);
        if (history.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(history);
    }
}
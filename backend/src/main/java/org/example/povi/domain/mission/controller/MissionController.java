package org.example.povi.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.auth.util.SecurityUtil;
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest;
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest;
import org.example.povi.domain.mission.dto.response.MissionResponse;
import org.example.povi.domain.mission.dto.response.MissionHistoryResponse;
import org.example.povi.domain.mission.service.MissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;


    // 오늘 미션 조회
    @GetMapping("/today")
    public ResponseEntity<List<MissionResponse>> getTodayMissions() {
        CustomJwtUser user = SecurityUtil.getCurrentUserOrThrow();
        List<MissionResponse> list = missionService.readTodayMissions(user.getId());
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }


    // 오늘 미션 생성
    @PostMapping("/today")
    public ResponseEntity<List<MissionResponse>> createTodayMissions(@RequestBody CreateTodayMissionsRequest req) {
        CustomJwtUser user = SecurityUtil.getCurrentUserOrThrow();
        List<MissionResponse> list = missionService.createTodayMissions(user.getId(), req.emotionType(), req.latitude(), req.longitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }


    // 유저 미션 상태 업데이트
    @PatchMapping("/{userMissionId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long userMissionId, @RequestBody @Valid UpdateStatusRequest req) {
        CustomJwtUser user = SecurityUtil.getCurrentUserOrThrow();
        missionService.updateUserMissionStatus(user.getId(), userMissionId, req.status());
        return ResponseEntity.noContent().build();
    }


    // 미션 이력 조회
    @GetMapping("/history")
    public ResponseEntity<List<MissionHistoryResponse>> getMissionHistory() {
        CustomJwtUser user = SecurityUtil.getCurrentUserOrThrow();
        List<MissionHistoryResponse> history = missionService.getMissionHistory(user.getId());
        if (history.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(history);
    }
}
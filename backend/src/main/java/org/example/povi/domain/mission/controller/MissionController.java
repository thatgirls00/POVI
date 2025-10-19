package org.example.povi.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.domain.mission.dto.request.CreateTodayMissionsRequest;
import org.example.povi.domain.mission.dto.request.UpdateStatusRequest;
import org.example.povi.domain.mission.dto.response.MissionResponse;
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
    public ResponseEntity<List<MissionResponse>> getTodayMissions(@RequestParam Long userId) {
        List<MissionResponse> list = missionService.readTodayMissions(userId);
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }


    // 오늘 미션 생성
    @PostMapping("/today")
    public ResponseEntity<List<MissionResponse>> createTodayMissions(@RequestBody CreateTodayMissionsRequest req) {
        List<MissionResponse> list = missionService.createTodayMissions(req.userId(), req.emotionType(), req.latitude(), req.longitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }


    // 유저 미션 상태 업데이트
    @PatchMapping("/{userMissionId}/status")
    public ResponseEntity<Void> updateStatus(@RequestParam Long userId, @PathVariable Long userMissionId, @RequestBody @Valid UpdateStatusRequest req) {
        missionService.updateUserMissionStatus(userId, userMissionId, req.status());
        return ResponseEntity.noContent().build();
    }
}
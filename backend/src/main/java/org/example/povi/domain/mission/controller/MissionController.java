package org.example.povi.domain.mission.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.User;
import org.example.povi.domain.mission.dto.MissionResponseDto;
import org.example.povi.domain.mission.dto.UserMissionResponseDto;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;
import org.example.povi.domain.mission.repository.UserMissionRepository;
import org.example.povi.domain.mission.service.MissionRecommendationService;
import org.example.povi.domain.mission.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<List<MissionResponseDto>> getTodayMissions(@RequestParam(value = "userId") Long userId, @RequestParam(value = "emotionType", required = false) Mission.EmotionType emotionType, @RequestParam(value = "latitude", required = false) Double latitude, @RequestParam(value = "longitude", required = false) Double longitude) {
        List<MissionResponseDto> todaytMissions = missionService.getTodaytMissions(userId, emotionType, latitude, longitude);
        //1. Req / Res 설정
        return ResponseEntity.ok().body(todaytMissions);
    }
}
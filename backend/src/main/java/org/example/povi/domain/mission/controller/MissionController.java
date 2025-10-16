package org.example.povi.domain.mission.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.User;
import org.example.povi.domain.mission.dto.UserMissionResponseDto;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;
import org.example.povi.domain.mission.repository.UserMissionRepository;
import org.example.povi.domain.mission.service.MissionRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionRecommendationService missionRecommendationService;
    private final UserMissionRepository userMissionRepository;

    // 감정 선택 및 미션 추천
    @PostMapping("/recommend")
    public ResponseEntity<List<UserMissionResponseDto>> recommendMissions(
            @RequestParam String emotionName,
            @RequestParam Long userId) {
        
        // 임시 User 객체 생성 (실제로는 인증에서 가져와야 함)
        User user = new User();
        // user.setId(userId); // 실제로는 UserRepository에서 조회해야 함
        
        // 감정 → Enum 매핑
        Mission.EmotionType emotionType = mapEmotionNameToType(emotionName);
        
        // 미션 추천
        List<UserMission> userMissions = missionRecommendationService.recommendDailyMissions(user, emotionType);
        userMissionRepository.saveAll(userMissions);
        
        // DTO 변환
        List<UserMissionResponseDto> response = userMissions.stream()
                .map(UserMissionResponseDto::new)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    // 오늘의 미션 조회
    @GetMapping("/today")
    public ResponseEntity<List<UserMissionResponseDto>> getTodayMissions(@RequestParam Long userId) {
        // 임시 User 객체 생성 (실제로는 UserRepository에서 조회해야 함)
        User user = new User();
        // user.setId(userId);
        
        LocalDate today = LocalDate.now();
        List<UserMission> todayMissions = userMissionRepository.findByUserAndCreatedAtBetween(
                user, today, today);
        
        List<UserMissionResponseDto> response = todayMissions.stream()
                .map(UserMissionResponseDto::new)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    // 미션 완료
    @PostMapping("/{userMissionId}/complete")
    public ResponseEntity<String> completeMission(@PathVariable Long userMissionId) {
        Optional<UserMission> userMissionOpt = userMissionRepository.findById(userMissionId);
        
        if (userMissionOpt.isPresent()) {
            UserMission userMission = userMissionOpt.get();
            userMission.completeMission();
            userMissionRepository.save(userMission);
            return ResponseEntity.ok("미션이 완료되었습니다.");
        }
        
        return ResponseEntity.notFound().build();
    }

    private Mission.EmotionType mapEmotionNameToType(String emotionName) {
        return switch (emotionName) {
            case "행복해요", "즐거워요" -> Mission.EmotionType.HAPPY;
            case "우울해요", "슬퍼요" -> Mission.EmotionType.SAD;
            case "평온해요", "그저그래요" -> Mission.EmotionType.CALM;
            case "힘들어요", "화나요" -> Mission.EmotionType.STRESSED;
            default -> Mission.EmotionType.CALM;
        };
    }
}
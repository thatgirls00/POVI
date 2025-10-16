package org.example.povi.domain.mission.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.User;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionRecommendationService {

    // 일일 미션 3개 추천
    public List<UserMission> recommendDailyMissions(User user, Mission.EmotionType emotionType) {
        // 1. 최근 감정 기반으로 미션 필터링
        List<Mission> candidateMissions = findMissionsByEmotion(emotionType);
        
        // 2. 날씨 조건 확인 (API 연동 부분) - 추후 구현
        // candidateMissions = filterByWeather(candidateMissions, weatherInfo);
        
        // 3. 유저의 과거 미션 완료 이력 고려
        candidateMissions = filterByUserHistory(candidateMissions, user);
        
        // 4. 상위 3개 선택
        List<Mission> selectedMissions = candidateMissions.stream()
                .limit(3)
                .collect(Collectors.toList());
        
        // 5. UserMission 객체 생성
        return selectedMissions.stream()
                .map(mission -> new UserMission(user, mission, emotionType))
                .collect(Collectors.toList());
    }

    // 감정 타입에 따른 미션 필터링
    private List<Mission> findMissionsByEmotion(Mission.EmotionType emotionType) {
        // 실제로는 MissionRepository에서 조회
        // 예시: return missionRepository.findByEmotionType(emotionType);
        return List.of(); // 임시
    }

    // 날씨 조건에 따른 미션 필터링 (추후 구현)
    // private List<Mission> filterByWeather(List<Mission> missions, String weatherInfo) {
    //     if (weatherInfo == null) return missions;
    //     
    //     return missions.stream()
    //             .filter(mission -> {
    //                 if (mission.getWeatherCondition() == null) return true;
    //                 return mission.getWeatherCondition().equals(weatherInfo);
    //             })
    //             .collect(Collectors.toList());
    // }

    // 유저 히스토리 기반 필터링
    private List<Mission> filterByUserHistory(List<Mission> missions, User user) {
        // 실제로는 UserMissionRepository에서 유저의 과거 미션 이력 조회
        return missions;
    }

}

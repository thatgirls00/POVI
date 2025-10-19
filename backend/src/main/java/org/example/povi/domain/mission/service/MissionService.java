package org.example.povi.domain.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.povi.domain.User;
import org.example.povi.domain.mission.dto.response.MissionResponse;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;
import org.example.povi.domain.mission.repository.MissionRepository;
import org.example.povi.domain.mission.repository.UserMissionRepository;
import org.example.povi.domain.mission.repository.UserRepository;
import org.example.povi.domain.weather.OpenWeatherClient;
import org.example.povi.domain.weather.WeatherTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private static final int DAILY_MISSION_COUNT = 3;

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final OpenWeatherClient weatherClient;
    private final WeatherTypeMapper weatherTypeMapper;

    /** 오늘 저장본 조회 */
    @Transactional(readOnly = true)
    public List<MissionResponse> readTodayMissions(Long userId) {
        User user = findUser(userId);
        LocalDate today = LocalDate.now();

        return userMissionRepository
                .findAllByUserAndMissionDateOrderByIdAsc(user, today)
                .stream()
                .map(um -> new MissionResponse(um.getMission(), um.getStatus()))
                .toList();
    }

    /** 오늘 최초 생성 (감정/위경도 필수) */
    @Transactional
    public List<MissionResponse> createTodayMissions(Long userId, Mission.EmotionType emotionType, Double latitude, Double longitude) {
        User user = findUser(userId);
        LocalDate today = LocalDate.now();

        if (userMissionRepository.existsByUserAndMissionDate(user, today)) {
            return readTodayMissions(userId);
        }
        if (emotionType == null || latitude == null || longitude == null) {
            throw new IllegalArgumentException("emotionType, latitude, longitude가 필요합니다.");
        }

        List<Mission> candidates;
        try {
            // 1) 정상 경로: 실시간 날씨 결정 → 정확 매칭만
            var snap = weatherClient.fetchSnapshot(latitude, longitude);
            var decided = weatherTypeMapper.decide(snap.weatherMain(), snap.temperatureC(), snap.windMs());
            log.info("[OW] main={}, tempC={}, windMs={}", snap.weatherMain(), snap.temperatureC(), snap.windMs());
            log.info("[OW] decidedWeather={}", decided);

            candidates = missionRepository.findByEmotionTypeAndWeatherTypeIn(
                    emotionType, java.util.List.of(decided));

            // 정확 매칭 정책: 후보가 0이면 데이터 보강 필요를 바로 알림
            if (candidates.isEmpty()) {
                throw new IllegalStateException("해당 감정/날씨 미션이 없습니다: emotion="
                        + emotionType + ", weather=" + decided + " — data.sql 보강 필요");
            }

        } catch (Exception e) {
            // 2) 비상 경로: API 실패/매핑 불가 시에만 감정+ANY로 대체
            log.warn("[OW] weather fetch/mapping failed, fallback to ANY: {}", e.toString());
            candidates = missionRepository.findByEmotionTypeAndWeatherTypeIn(
                    emotionType, java.util.List.of(Mission.WeatherType.ANY));

            if (candidates.isEmpty()) {
                // ANY마저 없으면 리턴할 게 없으므로 실패
                throw new IllegalStateException("비상 대체(ANY) 미션도 없습니다: emotion=" + emotionType);
            }
        }

        java.util.Collections.shuffle(candidates);
        var picked = candidates.stream().limit(DAILY_MISSION_COUNT).toList();

        var saved = picked.stream()
                .map(m -> new UserMission(user, m, today))
                .toList();
        userMissionRepository.saveAll(saved);

        return saved.stream()
                .map(um -> new MissionResponse(um.getMission(), um.getStatus()))
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));
    }

    @Transactional
    public void updateUserMissionStatus(Long userId, Long userMissionId, UserMission.MissionStatus status) {
        User user = findUser(userId);
        UserMission userMission = userMissionRepository.findByIdAndUser(userMissionId, user)
                .orElseThrow(() -> new IllegalArgumentException("UserMission을 찾을 수 없습니다. userMissionId=" + userMissionId));

        if (status == UserMission.MissionStatus.COMPLETED) {
            userMission.completeMission();
        } else if (status == UserMission.MissionStatus.IN_PROGRESS) {
            userMission.inProgressMission();
        } else {
            throw new IllegalArgumentException("허용되지 않는 상태값입니다: " + status);
        }
    }
}
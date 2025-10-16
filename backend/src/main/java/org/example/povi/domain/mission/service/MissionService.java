package org.example.povi.domain.mission.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.User;
import org.example.povi.domain.mission.dto.MissionResponseDto;
import org.example.povi.domain.mission.entity.Mission;
import org.example.povi.domain.mission.entity.UserMission;
import org.example.povi.domain.mission.repository.MissionRepository;
import org.example.povi.domain.mission.repository.UserMissionRepository;
import org.example.povi.domain.mission.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public List<MissionResponseDto> getTodaytMissions(Long userId, Mission.EmotionType emotionType, Double latitude, Double longitude) {


        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //1. 오늘의 미션을 처음 조회하는 샛기
        LocalDateTime now = LocalDateTime.now();
        boolean isFirstTime = userMissionRepository.existsByUserAndCreatedAt(userRepository, now);
//        if (isFirstTime) {
//            // => UserMission테이블에 Mission을 저장해야 함.
//            //1-0. 날씨 api와 emotionType을 조합해야함.(api 통해서)
//            //1-1. Mission테이블에서 emotionType에 맞는 mission들을 조회해옴.
//            //1-2. UserMission에 해당 Mission들을 저장함.
//            List<Mission> allByEmotionType = missionRepository.findAllByEmotionType(emotionType, weatherType);
//            // Mission -> UserMission 변환
//            List<UserMission> userMissions = allByEmotionType.stream()
//                    .map(mission -> new UserMission(user, mission)).toList();
//            userMissionRepository.saveAll(userMissions);
//
//        }

        //2. 오늘의 미션을 이미 조회한 샛기
        //=> 이미 Mission이 UserMission에 저장되어 있음. 그래서 단순 조회만 하면 됨.
        List<MissionResponseDto> responseDtoList = userMissionRepository.findAll().stream().map(mission -> new MissionResponseDto(mission.getMission(), mission.getStatus())).toList();
        return responseDtoList;
    }
}

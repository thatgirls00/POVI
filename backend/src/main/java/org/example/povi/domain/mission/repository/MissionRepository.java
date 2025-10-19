package org.example.povi.domain.mission.repository;

import org.example.povi.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    // 감정 + 날씨 조회
    List<Mission> findByEmotionTypeAndWeatherTypeIn(
            Mission.EmotionType emotionType,
            Collection<Mission.WeatherType> weatherTypes
    );
}

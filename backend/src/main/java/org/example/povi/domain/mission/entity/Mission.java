package org.example.povi.domain.mission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.example.povi.global.entity.BaseEntity;

@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AttributeOverride(name = "id", column = @Column(name = "mission_id"))
public class Mission extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false, length = 30)
    private EmotionType emotionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_type")   // 필요하면 nullable 설정
    private WeatherType weatherType;


    public enum EmotionType {
        HAPPY, JOYFUL, CALM, NEUTRAL, DEPRESSED, SAD, TIRED, ANGRY
    }

    public enum WeatherType {
        CLEAR,        // 맑음
        CLOUDY,       // 구름
        RAINY,        // 비
        SNOWY,        // 눈
        THUNDER,      // 뇌우
        DRIZZLE,      // 이슬비
        FOGGY,        // 안개/연무
        WINDY,        // 바람 강함
        HOT,          // 덥다 (예: T >= 28°C)
        COLD,         // 춥다 (예: T <= 5°C)
        ANY           // 어떤 날씨에도 추천 가능
    }
}
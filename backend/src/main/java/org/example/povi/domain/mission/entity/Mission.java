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
public class Mission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false, length = 30)
    private EmotionType emotionType;

    // 생성자(필요한 값만 받기)
    public Mission(String title, String description, EmotionType emotionType) {
        this.title = title;
        this.description = description;
        this.emotionType = emotionType;
    }

    public enum EmotionType {
        HAPPY, SAD, CALM, GRATEFUL, EXCITED, ANXIOUS, LONELY, STRESSED
    }
}
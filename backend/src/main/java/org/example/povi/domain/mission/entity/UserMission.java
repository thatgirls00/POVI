package org.example.povi.domain.mission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.User;              // 형님 프로젝트의 User 경로 그대로
import org.example.povi.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_missions")
@Getter
@NoArgsConstructor
public class UserMission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MissionStatus status = MissionStatus.IN_PROGRESS;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false)
    private Mission.EmotionType emotionType; // 배정 시점의 감정

    public enum MissionStatus {
        IN_PROGRESS, // 진행중
        COMPLETED    // 완료
    }

    // 필요한 값만 받는 생성자 (createdAt은 BaseEntity가 관리)
    public UserMission(User user, Mission mission, Mission.EmotionType emotionType) {
        this.user = user;
        this.mission = mission;
        this.emotionType = emotionType;
        this.status = MissionStatus.IN_PROGRESS;
    }

    // 미션 완료 (최소 도메인 동작)
    public void completeMission() {
        this.status = MissionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
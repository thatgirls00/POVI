package org.example.povi.domain.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.User;
import org.example.povi.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "user_missions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_date", "mission_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_mission_id"))
public class UserMission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MissionStatus status = MissionStatus.IN_PROGRESS;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;


    public enum MissionStatus {
        IN_PROGRESS, // 진행중
        COMPLETED    // 완료
    }

    // 필요한 값만 받는 생성자
    public UserMission(User user, Mission mission, LocalDate missionDate) {
        this.user = user;
        this.mission = mission;
        this.missionDate = missionDate;
        this.status = MissionStatus.IN_PROGRESS;
    }

    // 미션 완료
    public void completeMission() {
        this.status = MissionStatus.COMPLETED;
    }
    // 미션 진행중으로 되돌리기
    public void inProgressMission() {
        this.status = MissionStatus.IN_PROGRESS;
    }
}
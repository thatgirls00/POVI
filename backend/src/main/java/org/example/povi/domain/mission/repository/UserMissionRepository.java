package org.example.povi.domain.mission.repository;

import org.example.povi.domain.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserAndCreatedAt(UserRepository userRepository, LocalDateTime now);
}

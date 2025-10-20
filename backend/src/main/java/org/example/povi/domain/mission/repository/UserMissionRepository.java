package org.example.povi.domain.mission.repository;

import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserAndMissionDate(User user, LocalDate missionDate);

    // 화면에 항상 같은 순서로 보여주기 위해 정렬 추가
    List<UserMission> findAllByUserAndMissionDateOrderByIdAsc(User user, LocalDate missionDate);

    Optional<UserMission> findByIdAndUser(Long id,User user);
}

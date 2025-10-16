package org.example.povi.domain.mission.repository;

import org.example.povi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

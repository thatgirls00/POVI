package org.example.povi.domain.transcription.repository;

import org.example.povi.domain.transcription.entity.Transcription;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptionRepository extends JpaRepository<Transcription,Long> {
    void deleteAllByUser(User user);

    List<Transcription> findAllByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

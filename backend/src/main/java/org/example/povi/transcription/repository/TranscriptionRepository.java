package org.example.povi.transcription.repository;

import org.example.povi.domain.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptionRepository extends JpaRepository<Transcription,Long> {

    List<Transcription> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

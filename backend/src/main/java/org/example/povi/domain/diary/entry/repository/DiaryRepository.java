package org.example.povi.domain.diary.entry.repository;

import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    Optional<DiaryEntry> findByIdAndUserId(Long diaryId, Long userId);
    List<DiaryEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<DiaryEntry> findByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}

package org.example.povi.domain.diary.post.repository;

import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryPostRepository extends JpaRepository<DiaryPost, Long> {
    Optional<DiaryPost> findByIdAndUserId(Long diaryId, Long userId);
    List<DiaryPost> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<DiaryPost> findByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}

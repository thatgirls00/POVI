package org.example.povi.domain.diary.entry.repository;

import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
}

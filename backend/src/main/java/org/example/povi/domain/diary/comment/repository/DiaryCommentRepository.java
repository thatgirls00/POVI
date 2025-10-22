package org.example.povi.domain.diary.comment.repository;

import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment , Long> {
}

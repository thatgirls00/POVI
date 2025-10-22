package org.example.povi.domain.diary.comment.repository;

import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {

    /**
     * 댓글 ID와 게시글 ID로 특정 댓글 조회
     */
    Optional<DiaryComment> findByIdAndPostId(Long commentId, Long postId);
}

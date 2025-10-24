package org.example.povi.domain.community.repository;

import org.example.povi.domain.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
}

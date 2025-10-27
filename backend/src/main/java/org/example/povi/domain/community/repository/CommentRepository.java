package org.example.povi.domain.community.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.example.povi.domain.community.entity.Comment;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteAllByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Comment> findById(Long id);

    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
}

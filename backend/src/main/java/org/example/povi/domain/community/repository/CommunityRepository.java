package org.example.povi.domain.community.repository;

import org.example.povi.domain.community.entity.CommunityPost;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {
    void deleteAllByUser(User user);

    Page<CommunityPost> findAllByUserId(Long userId, Pageable pageable);
}

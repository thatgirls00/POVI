package org.example.povi.domain.community.repository;

import java.util.Optional;
import org.example.povi.domain.community.entity.CommunityBookmark;
import org.example.povi.domain.community.entity.CommunityPost;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {

    Optional<CommunityBookmark> findByUserAndCommunityPost(User user, CommunityPost communityPost);

    boolean existsByUserAndCommunityPost(User user, CommunityPost communityPost);

    //특정 사용자의 모든 북마크를 페이징하여 조회
    //Page<CommunityBookmark> findByUserId(Long userId, Pageable pageable);
}

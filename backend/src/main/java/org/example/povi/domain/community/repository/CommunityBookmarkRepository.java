package org.example.povi.domain.community.repository;

import java.util.Optional;
import org.example.povi.domain.community.entity.CommunityBookmark;
import org.example.povi.domain.community.entity.CommunityPost;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {
    void deleteAllByUser(User user);

    Optional<CommunityBookmark> findByUserAndCommunityPost(User user, CommunityPost communityPost);

    boolean existsByUserAndCommunityPost(User user, CommunityPost communityPost);

    @Query("SELECT b.communityPost FROM CommunityBookmark b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    Page<CommunityPost> findBookmarkedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}

package org.example.povi.domain.community.repository;

import java.util.Optional;
import org.example.povi.domain.community.entity.CommunityPost;
import org.example.povi.domain.community.entity.PostLike;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    void deleteAllByUser(User user);

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT pl.post FROM PostLike pl WHERE pl.user.id = :userId ORDER BY pl.createdAt DESC")
    Page<CommunityPost> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
